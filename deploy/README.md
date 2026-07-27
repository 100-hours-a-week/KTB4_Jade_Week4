# 배포 가이드 — EC2 1대 + Docker Compose + Nginx

CI/CD 없이 **로컬에서 이미지를 빌드해 GHCR에 push**하고, EC2에서 `pull` 후 실행한다.

```
        [내 노트북]                      [GHCR]                [EC2]
  docker build --platform amd64  →  banteum-backend:v0.1.0  →  docker compose pull
                                    banteum-frontend:v0.1.0     docker compose up -d
```

EC2에는 소스가 없고 `docker-compose.yml`, `nginx.conf`, `.env` 3개만 둔다.

## 컨테이너 구성

| 서비스 | 이미지 | 외부 노출 |
|---|---|---|
| `nginx` | `nginx:1.27-alpine` | **80** |
| `frontend` | `ghcr.io/.../banteum-frontend:<tag>` | 없음 |
| `app` | `ghcr.io/.../banteum-backend:<tag>` | 없음 |
| `mysql` | `mysql:8.4` | 없음 |

`app`과 `mysql`은 호스트 포트를 열지 않는다. 외부에서 DB에 직접 붙을 수 없다.

라우팅:
- `/api/**` → `app:8080` (Spring `context-path: /api`)
- 그 외 → `frontend:80` (React)

## 1. 이미지 빌드 & push (로컬)

GHCR 로그인은 최초 1회. `write:packages` 권한의 Personal Access Token이 필요하다.

```bash
echo $GHCR_TOKEN | docker login ghcr.io -u <github-username> --password-stdin
```

빌드·푸시. **Apple Silicon이면 `--platform linux/amd64`가 필수다.** 빼면 arm64 이미지가 올라가 EC2(x86)에서 실행되지 않는다.

```bash
VERSION=v0.1.0
SHA=$(git rev-parse --short HEAD)
IMAGE=ghcr.io/100-hours-a-week/banteum-backend

docker build --platform linux/amd64 -t $IMAGE:$VERSION -t $IMAGE:$SHA .
docker push $IMAGE:$VERSION
docker push $IMAGE:$SHA
```

### 태그 규칙

- `v0.1.0` — 배포 단위. `.env`의 `BACKEND_TAG`에 적는 값
- `<커밋 SHA>` — 같은 버전을 다시 빌드했을 때 어느 커밋인지 추적용

`latest`는 쓰지 않는다. 서버에 무엇이 떠 있는지 알 수 없고 롤백이 불가능해진다.

## 2. EC2 준비 (최초 1회)

- Docker + Docker Compose 설치
- 보안 그룹: 80(HTTP), 22(SSH, 내 IP만) 개방. 3306은 열지 않는다
- **IAM 역할 부착** — `s3:PutObject` 정책. 액세스 키를 서버에 두지 않기 위함
- 파일 배치

```bash
mkdir -p ~/deploy && cd ~/deploy
# docker-compose.yml, nginx.conf 복사 후
cp .env.example .env && chmod 600 .env   # 값 채우기
```

GHCR 이미지가 private이면 EC2에서도 `docker login ghcr.io` 필요 (`read:packages` 토큰).

## 3. 배포

```bash
cd ~/deploy
docker compose pull
docker compose up -d
docker compose ps
docker compose logs -f app
```

## 4. 롤백

`.env`의 `BACKEND_TAG`를 이전 버전으로 바꾸고 다시 올린다.

```bash
sed -i 's/^BACKEND_TAG=.*/BACKEND_TAG=v0.1.0/' .env
docker compose up -d
```

---

## 배포 전 확인할 것

### 첫 배포 시 스키마

prod는 `ddl-auto: validate`라 **테이블이 없으면 부팅에 실패한다.** 둘 중 하나:

- 첫 배포에 한해 `application-prod.yml`을 `update`로 올려 스키마를 만든 뒤 `validate`로 되돌린다
- 로컬 MySQL에서 스키마를 덤프해 `mysql` 컨테이너에 미리 넣는다

### HTTPS

`COOKIE_SECURE=true`는 HTTPS에서만 동작한다. IP로만 접속하는 초기 단계에서는 `.env`에 `COOKIE_SECURE=false`, `COOKIE_SAME_SITE=Lax`로 두고, 도메인 + 인증서를 붙인 뒤 `true`/`None`으로 바꾼다. HTTP에서 `true`로 두면 쿠키가 저장되지 않아 로그인이 되지 않는다.

### 데이터 백업

MySQL 데이터는 EC2 호스트의 named volume(`mysql-data`)에 있다. **인스턴스를 종료하면 함께 사라진다.** RDS와 달리 자동 스냅샷이 없으므로 주기적 덤프를 걸어둔다.

```bash
docker compose exec mysql mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" banteum > backup-$(date +%F).sql
```

### 메모리

t2.micro(1GB)에 nginx + React + Spring + MySQL 4개는 빠듯하다. 빌드는 로컬에서 하므로 실행만 하면 되지만, 스왑 2GB 정도는 잡아두는 편이 안전하다.

```bash
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
```
