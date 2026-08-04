#!/usr/bin/env bash
# 워밍업 대상 선정 실험. 조건 A/B/C를 무작위 순서로 반복 측정한다.
#
#   ./loadtest/warmup-target-experiment.sh 4 4      # 4회차부터 4회씩 추가
#
# 조건
#   A  워밍업 없음
#   B  목록 조회만 워밍업
#   C  목록 + 상세 조회 워밍업
#
# 회차마다 app 컨테이너를 재생성해 JVM을 완전히 초기화한다.
# 순서를 섞는 이유는 머신 상태가 시간에 따라 변할 때 특정 조건에만 쏠리지 않게 하기 위해서다.
#
# 목적은 결과의 재현성 확인이다. Compiler.queue/CodeCache는 여기서 수집하지 않는다.
# 7회까지의 결과에서 조건 간 순서가 유지되면 그때 별도 실험으로 분석한다.
#
# 결과: loadtest/results/experiment/runs.jsonl (한 줄에 한 회차)
set -euo pipefail

cd "$(dirname "$0")/.."

START_ROUND="${1:-4}"
ROUNDS="${2:-4}"
COMPOSE="docker compose -f docker-compose.loadtest.yml"
BASE="http://localhost:8080/api"
WARMUP_SECRET="loadtest-warmup-secret"
WARMUP_COUNT=500
VUS=10
DURATION=3m

OUT_DIR="loadtest/results/experiment"
mkdir -p "$OUT_DIR"
chmod 777 "$OUT_DIR"
RUNS_FILE="$OUT_DIR/runs.jsonl"

LIST_ONLY='/articles?size=10'
BOTH='/articles?size=10,/articles/{articleUuid}'

run_once() {
  local cond="$1" round="$2"
  local label="${cond}-r${round}"
  local stamp
  stamp="$(date +%Y%m%d-%H%M%S)-${label}"

  echo "===== 조건 ${cond} / ${round}회차 ====="

  # 조건마다 워밍업 대상이 다르다. compose가 기동 시점에 읽으므로 미리 export한다.
  case "$cond" in
    A) export WARMUP_TARGETS="$BOTH" ;;   # 워밍업을 안 하므로 값은 무관
    B) export WARMUP_TARGETS="$LIST_ONLY" ;;
    C) export WARMUP_TARGETS="$BOTH" ;;
  esac

  $COMPOSE rm -sf app >/dev/null 2>&1
  $COMPOSE up -d app >/dev/null 2>&1

  local boot_seconds=0
  for i in $(seq 1 120); do
    local code
    code="$(curl -s -o /dev/null -w '%{http_code}' "$BASE/auth/csrf" || true)"
    if [ "$code" = "204" ]; then boot_seconds=$i; break; fi
    sleep 1
  done
  [ "$boot_seconds" -gt 0 ] || { echo "  기동 실패"; return 1; }
  echo "  기동 ${boot_seconds}초"

  local warmup_seconds=0
  local warmup_json='null'
  if [ "$cond" != "A" ]; then
    local start
    start=$(date +%s)
    warmup_json="$(curl -s -X POST -H "X-Warmup-Secret: ${WARMUP_SECRET}" \
      "$BASE/internal/warmup?count=${WARMUP_COUNT}")"
    warmup_seconds=$(( $(date +%s) - start ))
    echo "  워밍업 ${warmup_seconds}초"
  fi

  # 부하 중간(40초 시점)에 자원 사용량을 찍는다.
  ( sleep 40; docker stats --no-stream --format '{{.Name}} {{.CPUPerc}} {{.MemUsage}}' \
      loadtest-app loadtest-mysql > "$OUT_DIR/${stamp}-stats.txt" 2>&1 ) &

  $COMPOSE --profile locust run --rm -T locust \
    -f /mnt/locust/locustfile.py \
    --host http://app:8080/api \
    --headless --users "$VUS" --spawn-rate 5 --run-time "$DURATION" \
    --csv "/mnt/locust/results/experiment/${stamp}" \
    > "$OUT_DIR/${stamp}.log" 2>&1
  wait

  python3 loadtest/collect-run.py \
    --condition "$cond" --round "$round" --stamp "$stamp" \
    --boot-seconds "$boot_seconds" --warmup-seconds "$warmup_seconds" \
    --out-dir "$OUT_DIR" >> "$RUNS_FILE"

  tail -1 "$RUNS_FILE" | python3 -c "
import json,sys
d=json.loads(sys.stdin.read())
for name, m in d['metrics'].items():
    print(f\"  {name:<26} p50={m['p50']:<5} p95={m['p95']:<5} p99={m['p99']:<5} max={m['max']}\")
print('  cpu:', d['cpu'])
"
}

# 조건별로 ROUNDS회씩 만들고 순서를 섞는다.
# 중간에 끊겼을 때는 PLAN="C:4 B:4 B:6" 처럼 남은 회차만 지정해 이어서 돌린다.
plan=()
if [ -n "${PLAN:-}" ]; then
  for item in $PLAN; do plan+=("$item"); done
else
  for cond in A B C; do
    for r in $(seq "$START_ROUND" $((START_ROUND + ROUNDS - 1))); do
      plan+=("${cond}:${r}")
    done
  done
fi
# macOS 기본 bash 3.2에는 mapfile이 없다.
shuffled=()
while IFS= read -r line; do
  shuffled+=("$line")
done < <(printf '%s\n' "${plan[@]}" | sort -R)

echo "실행 순서: ${shuffled[*]}"
echo

for item in "${shuffled[@]}"; do
  run_once "${item%%:*}" "${item##*:}"
done

echo
echo "===== 집계 ====="
python3 loadtest/analyze-experiment.py "$RUNS_FILE"
