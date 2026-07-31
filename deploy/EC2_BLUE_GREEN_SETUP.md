# 프론트엔드·백엔드 블루-그린 EC2 통합 설정 지시서

프론트엔드의 기존 단독 지시서는 실행하지 않는다. 공용 Compose와 Nginx 설정은
백엔드 저장소가 관리하며, 백엔드 CD의 최초 실행이 기존 단일 컨테이너를 통합
Blue 구조로 옮긴다.

작업 위치는 `/home/ubuntu/deploy`다.

## 0. 먼저 확인할 것

EC2의 배포 디렉터리에 기존 운영 파일이 있어야 한다.

```bash
cd /home/ubuntu/deploy
test -f compose.yml || test -f docker-compose.yml
test -f .env
grep -E '^(BACKEND_TAG|FRONTEND_TAG)=' .env
docker compose ps
```

최초 전환 전 `.env`에는 기존 `BACKEND_TAG`, `FRONTEND_TAG`가 있어야 한다.
백엔드 CD가 두 값을 각각 Blue와 Green 태그의 초기값으로 복사한다.

프론트와 백엔드 워크플로가 같은 잠금 파일을 사용하는지도 확인한다.

```bash
grep -rn banteum-deploy.lock /home/ubuntu 2>/dev/null || true
```

두 워크플로 모두 `/var/lock/banteum-deploy.lock`을 사용해야 한다.

## 1. 현재 운영 설정 백업

```bash
cd /home/ubuntu/deploy
BACKUP_DIR="$HOME/backup-blue-green-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$BACKUP_DIR"
cp .env "$BACKUP_DIR"/
cp compose.yml "$BACKUP_DIR"/ 2>/dev/null || true
cp docker-compose.yml "$BACKUP_DIR"/ 2>/dev/null || true
cp -r nginx "$BACKUP_DIR"/ 2>/dev/null || true
cp nginx.conf "$BACKUP_DIR"/ 2>/dev/null || true
echo "$BACKUP_DIR"
```

`app.env`, `mysql.env`, 인증서 볼륨과 MySQL 볼륨은 변경하지 않는다.

## 2. 코드 배포 순서

1. 이 통합 설정이 포함된 백엔드 변경을 먼저 `master`에 반영한다.
2. 백엔드 CD가 성공할 때까지 프론트 CD는 실행하지 않는다.
3. 백엔드 CD 성공 후 프론트 CD를 실행한다.

최초 백엔드 CD는 다음 작업을 자동으로 수행한다.

- `BACKEND_TAG`를 `BACKEND_BLUE_TAG`, `BACKEND_GREEN_TAG`로 변환
- `FRONTEND_TAG`를 `FRONTEND_BLUE_TAG`, `FRONTEND_GREEN_TAG`로 변환
- 공용 `compose.yml`과 `nginx/conf.d` 구성
- `app-blue`, `frontend-blue` 실행
- Nginx를 두 Blue 서비스로 전환
- 기존 단일 `app`, `frontend` 컨테이너 제거
- 백엔드 새 버전을 `app-green`에 배포한 뒤 Green으로 전환

최초 전환에서 문제가 생기면 Actions의 백엔드 CD 로그를 확인하고 다음 단계로
진행하지 않는다.

## 3. 백엔드 CD 성공 후 검증

```bash
cd /home/ubuntu/deploy

docker compose config -q
docker compose ps app-blue app-green frontend-blue frontend-green nginx

cat active-color-backend
cat active-color-frontend
cat nginx/conf.d/backend-upstream.conf
cat nginx/conf.d/frontend-upstream.conf

docker compose exec -T nginx nginx -t
curl --fail --silent --output /dev/null https://banteum.click/api/auth/csrf
curl --fail --silent --output /dev/null https://banteum.click/
```

최초 백엔드 배포가 정상 완료됐다면 다음 상태가 된다.

```text
active-color-backend  = green
backend-upstream.conf = app-green:8080

active-color-frontend  = blue
frontend-upstream.conf = frontend-blue:80
```

백엔드는 최초 마이그레이션 직후 새 버전을 반대 색상인 Green에 한 번 더 배포하기
때문에 Green이 활성화된다. 프론트는 기존 버전을 Blue에서 계속 제공한다.

## 4. 프론트 워크플로 확인

프론트 CD는 다음 공용 경로를 사용해야 한다.

```text
/home/ubuntu/deploy/compose.yml
/home/ubuntu/deploy/.env
/home/ubuntu/deploy/active-color-frontend
/home/ubuntu/deploy/nginx/conf.d/frontend-upstream.conf
/var/lock/banteum-deploy.lock
```

프론트 워크플로는 공용 `compose.yml`을 사용한다. 프론트 CD는 `compose.yml`이나
`nginx/conf.d/default.conf`, `backend-upstream.conf`를 덮어쓰면 안 된다.

프론트 CD가 변경할 수 있는 대상은 다음뿐이다.

```text
.env의 FRONTEND_BLUE_TAG 또는 FRONTEND_GREEN_TAG
active-color-frontend
nginx/conf.d/frontend-upstream.conf
frontend-blue 또는 frontend-green 컨테이너
```

## 5. 프론트 최초 배포

4번 조건을 확인한 뒤 프론트 CD를 실행한다. 성공 후 다음을 확인한다.

```bash
cd /home/ubuntu/deploy

docker compose ps frontend-blue frontend-green nginx
cat active-color-frontend
cat nginx/conf.d/frontend-upstream.conf

docker compose exec -T nginx nginx -t
curl --fail --silent --output /dev/null https://banteum.click/
```

정상이라면 `frontend-green`이 생성되고 활성 색상이 Green으로 바뀐다.

## 6. 이후 배포

초기 설정은 다시 하지 않는다.

- 백엔드 CD는 비활성 `app-blue` 또는 `app-green`만 교체한다.
- 프론트 CD는 비활성 `frontend-blue` 또는 `frontend-green`만 교체한다.
- 두 CD는 같은 `flock`으로 동시 실행을 막는다.
- 각 CD는 자기 상태 파일과 upstream 파일만 변경한다.

## 7. 수동 롤백

### 백엔드

```bash
cd /home/ubuntu/deploy
PREV=blue

printf 'upstream backend_active {\n    server app-%s:8080;\n}\n' "$PREV" \
  > nginx/conf.d/backend-upstream.conf
docker compose exec -T nginx nginx -t
docker compose exec -T nginx nginx -s reload
printf '%s\n' "$PREV" > active-color-backend
curl --fail --silent --output /dev/null https://banteum.click/api/auth/csrf
```

### 프론트엔드

```bash
cd /home/ubuntu/deploy
PREV=blue

printf 'upstream frontend_active {\n    server frontend-%s:80;\n}\n' "$PREV" \
  > nginx/conf.d/frontend-upstream.conf
docker compose exec -T nginx nginx -t
docker compose exec -T nginx nginx -s reload
printf '%s\n' "$PREV" > active-color-frontend
curl --fail --silent --output /dev/null https://banteum.click/
```

`PREV`에는 실제로 되돌릴 컨테이너가 실행 중인 색상을 넣는다.

## 체크리스트

- [ ] 프론트의 기존 EC2 단독 지시서를 실행하지 않음
- [ ] 기존 Compose 파일, `.env`, Nginx 설정 백업
- [ ] 기존 `.env`에 `BACKEND_TAG`, `FRONTEND_TAG` 존재
- [ ] 백엔드 CD를 프론트 CD보다 먼저 실행
- [ ] `.env`에 Blue/Green 태그 네 개 존재
- [ ] `active-color-backend`, `active-color-frontend` 존재
- [ ] Backend/Frontend upstream 파일이 각각 존재
- [ ] 프론트 CD가 공용 Compose/Nginx 파일을 덮어쓰지 않음
- [ ] 두 워크플로가 같은 `/var/lock/banteum-deploy.lock` 사용
- [ ] API와 프론트 외부 확인 성공
