#!/usr/bin/env bash
# Locust가 계속 트래픽을 보내는 중 app-blue에서 app-green으로 전환한다.
#
#   ./loadtest/blue-green-switch-experiment.sh 1 7
#   PLAN="B:4 A:4" ./loadtest/blue-green-switch-experiment.sh
set -euo pipefail

cd "$(dirname "$0")/.."

START_ROUND="${1:-1}"
ROUNDS="${2:-7}"
COMPOSE_FILE="docker-compose.bluegreen-loadtest.yml"
COMPOSE=(docker compose -f "$COMPOSE_FILE")
OUT_DIR="loadtest/results/blue-green-switch"
NGINX_CONF_DIR="$OUT_DIR/nginx/conf.d"
export BLUEGREEN_NGINX_CONF_DIR="./$NGINX_CONF_DIR"

VUS="${VUS:-10}"
SPAWN_RATE="${SPAWN_RATE:-5}"
PRE_SWITCH_SECONDS="${PRE_SWITCH_SECONDS:-30}"
POST_SWITCH_SECONDS="${POST_SWITCH_SECONDS:-90}"
LOCUST_RUN_TIME="${LOCUST_RUN_TIME:-4m}"
WARMUP_COUNT="${WARMUP_COUNT:-500}"
WARMUP_SECRET="${WARMUP_SECRET:-loadtest-warmup-secret}"
export WARMUP_COUNT WARMUP_SECRET

mkdir -p "$OUT_DIR" "$NGINX_CONF_DIR"
chmod 777 "$OUT_DIR"

write_upstream() {
  local color="$1"
  local tmp
  tmp="$(mktemp "$NGINX_CONF_DIR/backend-upstream.XXXXXX")"
  if [ "$color" = "blue" ]; then
    cp loadtest/bluegreen-nginx/backend-upstream-blue.conf "$tmp"
  else
    cp loadtest/bluegreen-nginx/backend-upstream-green.conf "$tmp"
  fi
  mv "$tmp" "$NGINX_CONF_DIR/backend-upstream.conf"
}

prepare_nginx_conf() {
  cp loadtest/bluegreen-nginx/default.conf "$NGINX_CONF_DIR/default.conf"
  write_upstream blue
}

wait_health() {
  local service="$1"
  for i in $(seq 1 120); do
    if "${COMPOSE[@]}" exec -T "$service" wget -q -O /dev/null http://localhost:8080/api/auth/csrf; then
      echo "  ${service} health OK (${i}s)"
      return 0
    fi
    sleep 1
  done
  echo "  ${service} health failed"
  "${COMPOSE[@]}" logs --tail 80 "$service"
  return 1
}

reload_nginx() {
  "${COMPOSE[@]}" exec -T nginx nginx -t
  "${COMPOSE[@]}" exec -T nginx nginx -s reload
}

warmup_green() {
  "${COMPOSE[@]}" exec -T app-green sh -c \
    'wget -q -O - -T 180 --post-data= --header="X-Warmup-Secret: $WARMUP_SECRET" "http://localhost:8080/api/internal/warmup?count=${WARMUP_COUNT:-500}"'
}

run_once() {
  local condition="$1" round="$2"
  local label="${condition}-r${round}"
  local stamp
  stamp="$(date +%Y%m%d-%H%M%S)-${label}"
  local marker_host="$OUT_DIR/${stamp}-switch.marker"
  local marker_container="/mnt/locust/results/blue-green-switch/${stamp}-switch.marker"
  local meta_file="$OUT_DIR/${stamp}-meta.json"
  local locust_log="$OUT_DIR/${stamp}.log"

  echo "===== 조건 ${condition} / ${round}회차 ====="

  rm -f "$marker_host"
  prepare_nginx_conf

  "${COMPOSE[@]}" up -d mysql >/dev/null
  "${COMPOSE[@]}" rm -sf app-blue app-green nginx >/dev/null 2>&1 || true
  "${COMPOSE[@]}" up -d --force-recreate app-blue >/dev/null
  wait_health app-blue
  "${COMPOSE[@]}" up -d --no-deps nginx >/dev/null
  reload_nginx

  SWITCH_MARKER_FILE="$marker_container" "${COMPOSE[@]}" --profile locust run --rm -T \
    -e SWITCH_MARKER_FILE="$marker_container" \
    locust \
    -f /mnt/locust/locustfile-bluegreen.py \
    --host http://nginx/api \
    --headless --users "$VUS" --spawn-rate "$SPAWN_RATE" --run-time "$LOCUST_RUN_TIME" \
    --csv "/mnt/locust/results/blue-green-switch/${stamp}" \
    > "$locust_log" 2>&1 &
  local locust_pid=$!

  sleep "$PRE_SWITCH_SECONDS"

  local green_boot_start green_ready_at warmup_start warmup_end switch_at warmup_seconds
  green_boot_start="$(date +%s)"
  "${COMPOSE[@]}" rm -sf app-green >/dev/null 2>&1 || true
  "${COMPOSE[@]}" up -d --force-recreate app-green >/dev/null
  wait_health app-green
  green_ready_at="$(date +%s)"

  warmup_seconds=0
  if [ "$condition" = "B" ]; then
    warmup_start="$(date +%s)"
    warmup_green | tee "$OUT_DIR/${stamp}-warmup.json"
    echo
    warmup_end="$(date +%s)"
    warmup_seconds=$((warmup_end - warmup_start))
  fi

  if ! kill -0 "$locust_pid" 2>/dev/null; then
    echo "  Locust stopped before switch. Increase LOCUST_RUN_TIME."
    wait "$locust_pid"
    return 1
  fi

  write_upstream green
  reload_nginx
  switch_at="$(python3 -c 'import time; print(f"{time.time():.6f}")')"
  printf '%s\n' "$switch_at" > "$marker_host"
  echo "  switched to green at $switch_at"

  docker stats --no-stream loadtest-app-blue loadtest-app-green loadtest-mysql loadtest-nginx \
    > "$OUT_DIR/${stamp}-stats.txt" 2>&1 || true

  python3 - "$meta_file" <<PY
import json
import sys

path = sys.argv[1]
record = {
    "condition": "$condition",
    "round": int("$round"),
    "stamp": "$stamp",
    "pre_switch_seconds": int("$PRE_SWITCH_SECONDS"),
    "post_switch_seconds": int("$POST_SWITCH_SECONDS"),
    "locust_run_time": "$LOCUST_RUN_TIME",
    "vus": int("$VUS"),
    "spawn_rate": float("$SPAWN_RATE"),
    "green_boot_seconds": int("$green_ready_at") - int("$green_boot_start"),
    "warmup_seconds": int("$warmup_seconds"),
    "switch_epoch": float("$switch_at"),
}
with open(path, "w") as f:
    json.dump(record, f, ensure_ascii=False)
    f.write("\\n")
PY

  sleep "$POST_SWITCH_SECONDS"
  wait "$locust_pid"

  python3 loadtest/collect-bluegreen-switch-run.py \
    --meta "$meta_file" \
    --out-dir "$OUT_DIR" >> "$OUT_DIR/runs.jsonl"

  tail -1 "$OUT_DIR/runs.jsonl" | python3 -c "
import json,sys
d=json.loads(sys.stdin.read())
print('  green_boot_seconds:', d['green_boot_seconds'], 'warmup_seconds:', d['warmup_seconds'])
for name, m in d['metrics'].items():
    print(f\"  {name:<28} requests={m['requests']:<5} p95={m['p95']:<5} p99={m['p99']:<5} max={m['max']} failures={m['failures']}\")
"
}

prepare_nginx_conf
"${COMPOSE[@]}" build app-blue app-green

plan=()
if [ -n "${PLAN:-}" ]; then
  for item in $PLAN; do plan+=("$item"); done
else
  for cond in A B; do
    for r in $(seq "$START_ROUND" $((START_ROUND + ROUNDS - 1))); do
      plan+=("${cond}:${r}")
    done
  done
fi

shuffled="$(python3 - "${plan[@]}" <<'PY'
import random
import sys
items = sys.argv[1:]
random.shuffle(items)
print("\n".join(items))
PY
)"

echo "실행 순서:"
printf '  %s\n' $shuffled
echo

for item in $shuffled; do
  run_once "${item%%:*}" "${item##*:}"
done

echo
echo "===== 집계 ====="
python3 loadtest/analyze-bluegreen-switch.py "$OUT_DIR/runs.jsonl"
