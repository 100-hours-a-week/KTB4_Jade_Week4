# 배포 가이드 — EC2 1대 + Docker Compose + Nginx

서비스 주소: **https://banteum.click**

CI/CD 없이 **로컬에서 이미지를 빌드해 GHCR에 push**하고, EC2에서 `pull` 후 실행한다.

```
        [로컬]                       [Docker Hub]              [EC2]
  docker build --platform amd64  →  banteum-backend:v0.1.0  →  docker compose pull
                                    banteum-frontend:v0.1.0     docker compose up -d
```

EC2에는 애플리케이션 소스 대신 Compose 설정, nginx 설정, 환경 파일만 둔다.

## 컨테이너 구성

| 서비스 | 이미지 | 외부 노출 |
|---|---|---|
| `nginx` | `nginx:1.27-alpine` | **80, 443** |
| `frontend` | `jeongminju45/banteum-frontend:<tag>` | 없음 |
| `app` | `jeongminju45/banteum-backend:<tag>` | 없음 |
| `mysql` | `mysql:8.4` | 없음 |
| `certbot` | `certbot/certbot` | 없음 |

`app`과 `mysql`은 호스트 포트를 열지 않는다. 외부에서 DB에 직접 붙을 수 없다.

라우팅:
- `/api/**` → `app:8080` (Spring `context-path: /api`)
- 그 외 → `frontend:80` (React)
- HTTP(80) 요청은 전부 HTTPS(443)로 301 리다이렉트. 단 `/.well-known/acme-challenge/`는 예외(인증서 갱신용)

---

## 1. 이미지 빌드 & push (로컬)

Docker Hub 로그인은 최초 1회. Account settings → Personal access tokens에서 Read/Write 토큰을 만들어 쓴다.

```bash
docker login -u jeongminju45
```

**Apple Silicon이면 `--platform linux/amd64`가 필수다.** 빼면 arm64 이미지가 올라가 EC2(x86)에서 `exec format error`로 실행되지 않는다.

```bash
VERSION=v0.1.0
SHA=$(git rev-parse --short HEAD)
IMAGE=jeongminju45/banteum-backend

docker build --platform linux/amd64 -t $IMAGE:$VERSION -t $IMAGE:$SHA .
docker push $IMAGE:$VERSION
docker push $IMAGE:$SHA
```

### 태그 규칙

- `v0.1.0` — 배포 단위. `.env`의 `BACKEND_TAG`에 적는 값
- `<커밋 SHA>` — 같은 버전을 다시 빌드했을 때 어느 커밋인지 추적용

`latest`는 쓰지 않는다. 서버에 무엇이 떠 있는지 알 수 없고 롤백이 불가능해진다.

---

## 2. 첫 배포

EC2 인스턴스 생성·IAM 역할·Docker 설치는 별도 문서(`ec2-setup.md`)를 따른다. 아래는 그 준비가 끝난 뒤부터다.

이미지가 Docker Hub public이므로 EC2에서는 로그인 없이 pull된다. 서버에 토큰을 두지 않아도 되고, 만료로 배포가 깨질 일도 없다.

### 파일 배치

```bash
scp -i <키>.pem docker-compose.yml nginx.conf nginx-http-only.conf .env.example ubuntu@<IP>:~/deploy/
```

```bash
cd ~/deploy
mv .env.example .env && chmod 600 .env
nano .env

# 최초 인증서 발급 전에는 443 설정으로 nginx를 띄울 수 없다.
cp nginx.conf nginx-https.conf
cp nginx-http-only.conf nginx.conf
```

`JWT_SECRET`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`는 새로 생성한다. 로컬 값을 재사용하지 않는다.

```bash
openssl rand -base64 48 | tr -d '\n'
```

**AWS 액세스 키는 넣지 않는다.** EC2에 붙인 IAM 역할이 대신한다.

### 스키마 생성 — 첫 배포에만 필요

운영은 `ddl-auto: validate`다. 엔티티와 실제 테이블이 어긋나면 부팅을 거부해서, 잘못된 스키마 위에서 앱이 도는 것을 막는다.

그런데 첫 배포는 DB가 비어 있어 검사할 테이블이 없다. 그대로 올리면 이렇게 죽는다.

```
Schema-validation: missing table [article]
```

그래서 첫 기동만 `update`로 스키마를 만들고 곧바로 되돌린다. yml은 환경변수를 받으므로(`ddl-auto: ${JPA_DDL_AUTO:validate}`) 수정할 필요가 없다.

```bash
# .env
JPA_DDL_AUTO=update
```

```bash
docker compose pull
docker compose up -d
docker compose logs -f app        # "Started TaskApplication" 확인
```

**확인되면 반드시 되돌린다.**

```bash
sed -i 's/^JPA_DDL_AUTO=.*/JPA_DDL_AUTO=validate/' .env
docker compose up -d app
```

`update`로 계속 두면 엔티티를 잘못 고쳤을 때 운영 스키마가 조용히 따라 바뀐다.

### 확인

```bash
curl -i https://banteum.click/api/auth/csrf   # 204, Secure 쿠키
curl -i https://banteum.click/                # React
curl -i http://banteum.click/                 # 301 → https
```

---

## HTTPS (Let's Encrypt)

ALB가 없어 ACM을 쓸 수 없으므로 certbot으로 발급한다. `certbot` 컨테이너가 12시간마다 깨어나 만료가 가까우면 자동 갱신한다.

**최초 발급 순서** (인증서가 없는 상태에서 443 설정을 올리면 nginx가 파일을 못 찾아 죽는다)

1. HTTP-only 설정으로 nginx 기동
   ```bash
   cp nginx-http-only.conf nginx.conf
   docker compose up -d nginx
   ```
2. 인증서 발급
   ```bash
   docker compose run --rm --entrypoint certbot certbot certonly \
     --webroot -w /var/www/certbot -d banteum.click \
     --email <메일> --agree-tos --no-eff-email --non-interactive
   ```
3. HTTPS 설정을 복원하고 nginx 재기동
   ```bash
   cp nginx-https.conf nginx.conf
   docker compose restart nginx
   ```

**인증서 상태 확인**

```bash
docker compose run --rm --entrypoint certbot certbot certificates
echo | openssl s_client -connect banteum.click:443 -servername banteum.click 2>/dev/null \
  | openssl x509 -noout -dates
```

**도메인을 바꾸거나 추가할 때 손봐야 하는 곳**

1. Route 53 A 레코드
2. certbot 발급 (`-d` 추가)
3. `nginx.conf`의 `server_name`
4. `.env`의 `CORS_ALLOWED_ORIGINS`
5. **S3 prod 버킷 CORS의 `AllowedOrigins`** ← 빠뜨리면 이미지 업로드만 실패한다

---

## 3. 이후 배포

```bash
cd ~/deploy
sed -i 's/^BACKEND_TAG=.*/BACKEND_TAG=v0.2.0/' .env
docker compose pull
docker compose up -d
```

DB 볼륨(`mysql-data`)은 유지되므로 데이터는 그대로다. `JPA_DDL_AUTO`는 `validate`로 고정.

| | 첫 배포 | 이후 배포 |
|---|---|---|
| `JPA_DDL_AUTO` | `update` → 확인 후 `validate` | `validate` |
| DB | 빈 상태 | 데이터 유지 |
| EC2 준비 | 필요 | 불필요 |

### 엔티티를 변경했다면

`validate`는 컬럼을 만들어주지 않으므로 그대로 배포하면 부팅에 실패한다. 배포 전에 스키마를 먼저 맞춘다.

```bash
docker compose exec mysql mysql -u root -p banteum
```

변경 SQL을 직접 실행하는 쪽을 권한다. 무엇이 바뀌는지 눈으로 확인할 수 있다.
급하면 그 배포에 한해 `JPA_DDL_AUTO=update`로 올린 뒤 되돌려도 되지만, **컬럼 삭제·타입 변경은 `update`가 처리하지 못한다.**

---

## 4. 롤백

```bash
sed -i 's/^BACKEND_TAG=.*/BACKEND_TAG=v0.1.0/' .env
docker compose up -d
```

이미지 태그를 버전으로 고정해 두었기 때문에 가능하다.

**주의**: 스키마를 바꾼 뒤 롤백하면 구버전 코드가 새 스키마를 만나 `validate`에 실패할 수 있다. 스키마 변경 배포는 롤백 계획을 같이 세운다.

---

## 운영 참고

### 백업

MySQL 데이터는 EC2 호스트의 named volume(`mysql-data`)에 있다. **인스턴스를 종료하면 함께 사라진다.** RDS와 달리 자동 스냅샷이 없으므로 주기적 덤프를 걸어둔다. 배포 전에 한 번 떠두면 안전하다.

```bash
docker compose exec mysql mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" banteum > backup-$(date +%F).sql
aws s3 cp backup-$(date +%F).sql s3://ktb-banteum-prod/backup/
```

### 쿠키

현재 `COOKIE_SECURE=true`, `COOKIE_SAME_SITE=Lax`.

`Secure` 쿠키는 HTTPS에서만 전송된다. HTTP로만 접속하는 환경에서 `true`로 두면 브라우저가 쿠키를 저장하지 않아 **로그인이 되지 않는다.** 프론트와 API가 Nginx 뒤에서 같은 오리진이므로 `SameSite`는 `Lax`로 충분하다.

### 메모리

t3.micro(1GB)에 컨테이너 4개는 빠듯하다. 빌드는 로컬에서 하므로 실행만 하면 되지만, 스왑 2GB 정도는 잡아두는 편이 안전하다.

```bash
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 이미지 업로드가 500이면

컨테이너가 AWS 자격증명을 찾지 못한 것이다. EC2에 IAM 역할(`s3:PutObject`)이 붙어 있는지 확인한다.

```bash
TOKEN=$(curl -sX PUT "http://169.254.169.254/latest/api/token" \
  -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/meta-data/iam/security-credentials/
```

역할 이름이 나오지 않으면 EC2 콘솔 → 작업 → 보안 → IAM 역할 수정.
