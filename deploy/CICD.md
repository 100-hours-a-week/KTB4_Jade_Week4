# CI/CD 설정 가이드

GitHub Actions에서 이미지를 빌드해 Docker Hub에 push하고, **AWS SSM으로 EC2에 배포**한다.

SSH를 쓰지 않는다. Actions 러너는 IP가 매번 바뀌어서 보안 그룹 22번을 열어야 하는데, SSM은 포트를 열지 않고도 명령을 보낼 수 있고 실행 기록이 CloudTrail에 남는다.

```text
PR 또는 develop push
  → CI: 테스트

master push
  → 테스트
  → sha-{커밋 SHA} 이미지 빌드(linux/amd64)
  → Docker Hub push
  → OIDC로 AWS 임시 권한 획득
  → SSM으로 EC2 배포
  → inactive 색상(app-blue 또는 app-green)에 새 이미지 배포
  → 내부 헬스체크
  → Nginx upstream 전환
  → 외부 헬스체크
```

## 워크플로 2개

| 파일 | 언제 | 하는 일 |
|---|---|---|
| `ci.yml` | PR, develop push | 테스트만 |
| `deploy.yml` | master push, 수동 실행 | SHA 이미지 빌드·push → 배포 → 헬스체크 |

## 프론트엔드와의 분담

레포가 분리되어 있어 **각자 자기 컨테이너만 배포**한다.

| | 백엔드 레포 | 프론트 레포 |
|---|---|---|
| 이미지 | `banteum-backend` | `banteum-frontend` |
| `.env`에서 바꾸는 줄 | `BACKEND_BLUE_TAG` 또는 `BACKEND_GREEN_TAG` | `FRONTEND_BLUE_TAG` 또는 `FRONTEND_GREEN_TAG` |
| 재시작하는 컨테이너 | inactive 색상의 `app-blue` 또는 `app-green` | inactive 색상의 `frontend-blue` 또는 `frontend-green` |
| IAM 역할 | `ktb-banteum-github-actions` | 별도 생성 |

두 워크플로는 태그만 있는 `.env`에서 각자 담당하는 줄만 변경한다. 백엔드는 현재 active가 아닌 색상의 태그만 변경한다. IAM 역할을 분리한 이유는 나중에 한쪽 권한만 회수할 수 있게 하기 위해서다.

공용 `compose.yml`과 Nginx 기본 설정은 백엔드 레포가 관리한다. 각 워크플로는 자기 upstream과 상태 파일만 변경한다.

```text
/home/ubuntu/deploy/
├── .env                    # 프론트·백엔드 색상별 이미지 태그
├── active-color-backend    # 백엔드 active 색상
├── active-color-frontend   # 프론트 active 색상
├── nginx/conf.d/
│   ├── default.conf
│   ├── backend-upstream.conf
│   └── frontend-upstream.conf
├── app.env    # Spring 운영 설정과 JWT_SECRET
└── mysql.env  # MySQL 계정과 비밀번호
```

프론트 워크플로는 `app.env`, `mysql.env`에 접근하지 않는다.

프론트 담당에게 넘길 설정 가이드는 `docs/frontend-cicd.md`에 있다.

---

## 플레이스홀더

이 레포는 공개 저장소라 계정·리소스 식별자를 문서에 직접 쓰지 않는다.

| 플레이스홀더 | 어디서 확인 |
|---|---|
| `<AWS_ACCOUNT_ID>` | AWS 콘솔 오른쪽 위 계정 메뉴 |
| `<EC2_INSTANCE_ID>` | EC2 → 인스턴스 목록 |

실제 값은 커밋하지 않는 로컬 문서(`docs/`, gitignore 대상)에 둔다.

자격증명은 아니지만 표적 공격의 단서가 되므로 굳이 공개하지 않는다. 실제
비밀값(DB 비밀번호, JWT 시크릿, Docker Hub 토큰, AWS 키)은 이 문서는 물론
어떤 커밋에도 넣지 않는다.

---

## [공통] 1. EC2 IAM 역할에 SSM 권한 추가

아래 1~2번은 **계정에 한 번만** 하면 된다. 프론트도 같은 것을 쓴다.


SSM 에이전트는 Ubuntu AMI에 이미 설치·실행 중이다. 권한만 주면 된다.

```
IAM → 역할 → ktb-banteum-ec2-role → 권한 추가 → 정책 연결
→ AmazonSSMManagedInstanceCore
```

연결 후 확인:

```
Systems Manager → 플릿 관리자
```

인스턴스가 목록에 뜨면 준비 완료. 안 뜨면 몇 분 기다리거나 SSM 에이전트를 재시작한다.

## [공통] 2. GitHub OIDC 공급자 등록

```
IAM → 자격 증명 공급자 → 공급자 추가
```

| 항목 | 값 |
|---|---|
| 공급자 유형 | OpenID Connect |
| 공급자 URL | `https://token.actions.githubusercontent.com` |
| 대상(Audience) | `sts.amazonaws.com` |

## [백엔드] 3. Actions용 IAM 역할 생성

```
IAM → 역할 → 역할 생성 → 웹 자격 증명
→ 공급자: token.actions.githubusercontent.com
→ Audience: sts.amazonaws.com
```

역할 이름: `ktb-banteum-github-actions`

### 신뢰 정책

**이 레포의 `master` 브랜치에서만** 역할을 쓸 수 있도록 제한한다.

`sub` 조건이 아예 없으면 다른 레포에서도 이 역할을 가져갈 수 있다. 레포까지만 제한하고 `repo:...:*` 처럼 와일드카드를 쓰면 그 레포의 아무 브랜치에서나 역할을 가져갈 수 있어서, 브랜치 하나 푸시하는 것만으로 배포 권한을 얻는다. `ref:refs/heads/master`까지 명시한다.

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "Federated": "arn:aws:iam::<AWS_ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
    },
    "Action": "sts:AssumeRoleWithWebIdentity",
    "Condition": {
      "StringEquals": {
        "token.actions.githubusercontent.com:aud": "sts.amazonaws.com",
        "token.actions.githubusercontent.com:sub": "repo:100-hours-a-week/KTB4_Jade_Week4:ref:refs/heads/master"
      }
    }
  }]
}
```

### 권한 정책

특정 인스턴스에 셸 명령을 보내는 것만 허용한다.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "ssm:SendCommand",
      "Resource": [
        "arn:aws:ec2:ap-northeast-2:<AWS_ACCOUNT_ID>:instance/<EC2_INSTANCE_ID>",
        "arn:aws:ssm:ap-northeast-2::document/AWS-RunShellScript"
      ]
    },
    {
      "Effect": "Allow",
      "Action": ["ssm:GetCommandInvocation", "ssm:ListCommandInvocations"],
      "Resource": "*"
    }
  ]
}
```

역할 ARN을 복사해 둔다. (`arn:aws:iam::<AWS_ACCOUNT_ID>:role/ktb-banteum-github-actions`)

## [백엔드] 4. GitHub Secrets 등록

```
레포 → Settings → Secrets and variables → Actions → New repository secret
```

| Name | Value |
|---|---|
| `AWS_ROLE_ARN` | 3번에서 만든 역할 ARN |
| `EC2_INSTANCE_ID` | `<EC2_INSTANCE_ID>` |
| `DOCKERHUB_USERNAME` | `jeongminju45` |
| `DOCKERHUB_TOKEN` | Docker Hub → Account settings → Personal access tokens (Read/Write) |

**AWS 액세스 키는 등록하지 않는다.** OIDC로 매번 임시 자격증명을 받는다.

`BACKEND_BLUE_TAG`, `BACKEND_GREEN_TAG`는 Secrets에 넣지 않는다. GitHub Actions가 현재 커밋에서 `sha-a1b2c3d` 형태로 자동 생성하고 inactive 색상의 태그만 갱신한다.

---

## 배포하는 법

`master`에 코드가 push 또는 merge되면 자동으로 배포된다. 개발자가 버전 태그를 직접 만들 필요가 없다.

```text
master commit: a1b2c3d...
Docker image: jeongminju45/banteum-backend:sha-a1b2c3d
EC2 .env: inactive 색상의 BACKEND_*_TAG=sha-a1b2c3d
```

Actions 탭에서 진행 상황을 볼 수 있다. 완료되면 외부 헬스체크까지 통과한 상태다.

## 롤백

블루-그린 배포에서는 전환에 성공해도 이전 컨테이너를 바로 종료하지 않는다.
문제가 확인되면 Nginx upstream만 이전 색상으로 되돌린다.

```bash
cd /home/ubuntu/deploy
cat active-color-backend.bak
cp nginx/conf.d/backend-upstream.conf.bak nginx/conf.d/backend-upstream.conf
docker compose exec -T nginx nginx -t
docker compose exec -T nginx nginx -s reload
cp active-color-backend.bak active-color-backend
```

**주의**: 스키마를 바꾼 배포를 롤백하면 구버전 코드가 새 스키마를 만나 `validate`에 실패할 수 있다.

---

## 확인할 점

**엔티티를 변경했다면** 배포 전에 스키마를 먼저 맞춘다. `validate` 상태에서는 컬럼이 자동 생성되지 않아 부팅에 실패한다.

**`.env`는 서버에만 있다.** 워크플로는 inactive 색상의 백엔드 태그만 바꾼다. 다른 환경변수를 바꾸려면 서버에서 직접 수정해야 한다.
