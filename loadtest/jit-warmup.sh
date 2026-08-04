#!/usr/bin/env bash
# 워밍업만 수행하고 JIT 컴파일 로그를 수집한다. 부하는 걸지 않는다.
#
# 부하를 섞으면 워밍업으로 데워진 부분과 부하로 데워진 부분을 구분할 수 없다.
# 그래서 워밍업 직후의 컴파일 상태만 잘라낸다.
#
#   ./loadtest/jit-warmup.sh 1000 cpu1
#   ./loadtest/jit-warmup.sh 1000 cpu4 docker-compose.cpu4.yml
#
# 인자: <워밍업 횟수> <결과 라벨> [compose override 파일]
# 결과: loadtest/results/jit/jit-<라벨>.log
set -euo pipefail

cd "$(dirname "$0")/.."

COUNT="${1:?워밍업 횟수를 지정해라}"
LABEL="${2:?결과 라벨을 지정해라}"
OVERRIDE="${3:-}"
CONCURRENCY="${WARMUP_CONCURRENCY:-10}"

COMPOSE="docker compose -f docker-compose.loadtest.yml"
[ -n "$OVERRIDE" ] && COMPOSE="$COMPOSE -f $OVERRIDE"
BASE="http://localhost:8080/api"
OUT_DIR="loadtest/results/jit"
mkdir -p "$OUT_DIR"

echo "[1/4] app 컨테이너 재생성 (JVM을 완전히 새로 띄운다)"
$COMPOSE rm -sf app >/dev/null 2>&1
$COMPOSE up -d app >/dev/null 2>&1

for i in $(seq 1 90); do
  code="$(curl -s -o /dev/null -w '%{http_code}' "$BASE/auth/csrf" || true)"
  if [ "$code" = "204" ]; then echo "  기동 완료 (${i}초)"; break; fi
  sleep 1
  if [ "$i" = "90" ]; then echo "  기동 실패"; exit 1; fi
done

echo "[2/4] JVM이 잡은 컴파일러 스레드 수"
$COMPOSE exec -T app sh -c 'jcmd 1 VM.flags 2>/dev/null | tr " " "\n" | grep -i "CICompilerCount\|TieredCompilation"' | sed 's/^/  /' || true

echo "[3/4] 워밍업 ${COUNT}회 (동시성 ${CONCURRENCY})"
SUFFIX="$(date +%s)"
PASSWORD='JadeHello1234!'
curl -s -o /dev/null -X POST "$BASE/auth/sign-up" -H 'Content-Type: application/json' \
  -d "{\"email\":\"jit${SUFFIX}@kakaotech.com\",\"password\":\"$PASSWORD\",\"checkPassword\":\"$PASSWORD\",\"nickname\":\"jt${SUFFIX: -5}\",\"profileImageUrl\":\"https://example.invalid/profile/loadtest.webp\"}"
TOKEN="$(curl -s -i -X POST "$BASE/auth/sign-in" -H 'Content-Type: application/json' \
  -d "{\"email\":\"jit${SUFFIX}@kakaotech.com\",\"password\":\"$PASSWORD\"}" \
  | grep -i '^set-cookie: access_token=' | head -1 | sed -E 's/.*access_token=([^;]+).*/\1/' | tr -d '\r')"
[ -n "$TOKEN" ] || { echo "  로그인 실패"; exit 1; }

START=$(date +%s)
seq 1 "$COUNT" | xargs -P "$CONCURRENCY" -I{} \
  curl -s -o /dev/null "$BASE/articles?size=10" -H "Cookie: access_token=$TOKEN"
echo "  소요 $(( $(date +%s) - START ))초"

# 컴파일 큐에 남은 작업이 처리될 시간을 준다.
sleep 3

echo "[4/4] JIT 로그 수집"
$COMPOSE exec -T app sh -c 'cat /tmp/jit-compilation.log' > "$OUT_DIR/jit-${LABEL}.log"
echo "  $OUT_DIR/jit-${LABEL}.log ($(du -h "$OUT_DIR/jit-${LABEL}.log" | cut -f1))"
echo
echo "분석: python3 loadtest/analyze-jit.py $OUT_DIR/jit-${LABEL}.log"
