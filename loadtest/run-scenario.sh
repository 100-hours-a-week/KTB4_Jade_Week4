#!/usr/bin/env bash
# 서버를 완전히 새로 기동한 뒤 워밍업 N회 -> 부하테스트를 수행한다.
#
#   ./loadtest/run-scenario.sh 0      # Scenario 1 (워밍업 없음)
#   ./loadtest/run-scenario.sh 100    # Scenario 2
#   ./loadtest/run-scenario.sh 500    # Scenario 3
#
# 기본 동작: app 컨테이너만 재기동한다. JVM은 완전히 새로 뜨고 DB(시드 1000만 건)는 유지된다.
# RESET_DB=1을 주면 MySQL 볼륨까지 지운다. 시드를 다시 넣어야 하므로 평소에는 쓰지 않는다.
#
# 환경변수:
#   VUS(기본 5), SPAWN_RATE(기본 5), DURATION(기본 3m)
#   WARMUP_MODE=external|endpoint (기본 external)
#   RESET_DB(기본 0)
#
# 웹 UI로 직접 조작하려면 이 스크립트 대신:
#   docker compose -f docker-compose.loadtest.yml --profile locust up locust
#   http://localhost:8089
#
# 대상은 전부 로컬 컨테이너다. 원격/EC2에는 접근하지 않는다.
set -euo pipefail

WARMUP_COUNT="${1:-0}"
RESET_DB="${RESET_DB:-0}"
VUS="${VUS:-5}"
SPAWN_RATE="${SPAWN_RATE:-5}"
WARMUP_CONCURRENCY="${WARMUP_CONCURRENCY:-1}"
# external: 외부에서 curl 반복 / endpoint: 앱 내부 워밍업 엔드포인트 호출
WARMUP_MODE="${WARMUP_MODE:-external}"
WARMUP_SECRET="${WARMUP_SECRET:-loadtest-warmup-secret}"
DURATION="${DURATION:-3m}"
export VUS DURATION
COMPOSE="docker compose -f docker-compose.loadtest.yml"
BASE="http://localhost:8080/api"
RESULT_DIR="loadtest/results"
STAMP="$(date +%Y%m%d-%H%M%S)-warmup${WARMUP_COUNT}"

cd "$(dirname "$0")/.."
mkdir -p "$RESULT_DIR"
# locust 컨테이너는 uid 1000으로 돌아 호스트 소유자와 다르다. CSV를 쓰려면 쓰기 권한이 필요하다.
chmod 777 "$RESULT_DIR"

if [ "$RESET_DB" = "1" ]; then
  echo "[1/5] 전체 초기화 (MySQL 볼륨까지 삭제 — 시드 재적재 필요)"
  $COMPOSE down -v --remove-orphans
  $COMPOSE up -d --build mysql app
else
  echo "[1/5] app 컨테이너만 재기동 (DB 유지)"
  $COMPOSE up -d mysql
  # JVM을 확실히 새로 띄운다. 컨테이너를 재생성해야 JIT 캐시가 남지 않는다.
  $COMPOSE rm -sf app
  $COMPOSE up -d --build app
fi

echo "[2/5] health check 대기 (GET /auth/csrf)"
for i in $(seq 1 120); do
  code="$(curl -s -o /dev/null -w '%{http_code}' "$BASE/auth/csrf" || true)"
  if [ "$code" = "204" ] || [ "$code" = "200" ]; then
    echo "  기동 완료 (${i}초, HTTP $code)"
    break
  fi
  sleep 1
  if [ "$i" = "120" ]; then echo "  기동 실패"; $COMPOSE logs --tail 50 app; exit 1; fi
done

echo "[3/5] 테스트 계정 생성"
SUFFIX="$(date +%s)"
EMAIL="warmup${SUFFIX}@kakaotech.com"
PASSWORD='JadeHello1234!'
curl -s -o /dev/null -X POST "$BASE/auth/sign-up" -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\",\"checkPassword\":\"$PASSWORD\",\"nickname\":\"w${SUFFIX: -6}\",\"profileImageUrl\":\"https://example.invalid/profile/loadtest.webp\"}"
TOKEN="$(curl -s -i -X POST "$BASE/auth/sign-in" -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" \
  | grep -i '^set-cookie: access_token=' | head -1 | sed -E 's/.*access_token=([^;]+).*/\1/' | tr -d '\r')"
[ -n "$TOKEN" ] || { echo "  로그인 실패"; exit 1; }

echo "[4/5] 워밍업 ${WARMUP_COUNT}회 (${WARMUP_MODE}, 동시성 ${WARMUP_CONCURRENCY})"
if [ "$WARMUP_COUNT" -gt 0 ]; then
  start=$(date +%s)
  if [ "$WARMUP_MODE" = "endpoint" ]; then
    # 배포 스크립트가 실제로 쓸 방식. 앱이 자기 자신에게 요청을 보낸다.
    curl -s -X POST -H "X-Warmup-Secret: ${WARMUP_SECRET}" \
      "$BASE/internal/warmup?count=${WARMUP_COUNT}" | sed 's/^/  /'
    echo
  else
    # 외부에서 데우는 방식. 클라이언트가 별도 프로세스라 앱 CPU를 덜 뺏는다.
    seq 1 "$WARMUP_COUNT" | xargs -P "$WARMUP_CONCURRENCY" -I{} \
      curl -s -o /dev/null "$BASE/articles?size=10" -H "Cookie: access_token=$TOKEN"
  fi
  echo "  워밍업 소요 $(( $(date +%s) - start ))초"
fi

echo "[5/5] 부하테스트 시작 (VU=${VUS}, ${DURATION})"
# --csv는 ${STAMP}_stats_history.csv를 2초 간격으로 남긴다. 구간별 latency는 이 파일로 본다.
$COMPOSE --profile locust run --rm -T locust \
  -f /mnt/locust/locustfile.py \
  --host http://app:8080/api \
  --headless \
  --users "$VUS" \
  --spawn-rate "$SPAWN_RATE" \
  --run-time "$DURATION" \
  --csv "/mnt/locust/results/${STAMP}" \
  | tee "$RESULT_DIR/${STAMP}.log"

echo "docker stats 스냅샷:"
docker stats --no-stream loadtest-app loadtest-mysql
echo "JIT 로그:"
$COMPOSE exec -T app sh -c 'ls -lh /tmp/jit-compilation.log' || true
