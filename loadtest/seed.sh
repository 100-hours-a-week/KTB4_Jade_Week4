#!/usr/bin/env bash
# 게시글 시드 데이터를 적재한다. 기본 1만 건.
#
# 1만 건은 buffer pool(256M)에 전부 올라간다. 디스크 IO가 latency를 지배하지 않아야
# JVM 워밍업 신호를 볼 수 있다. 데이터량을 키우면 IO가 신호를 덮는다.
#
#   ./loadtest/seed.sh                  # 1만 건
#   TARGET_ROWS=10000000 ./loadtest/seed.sh   # 인덱스 검증용 별도 실험
#
# 전제: app이 한 번 떠야 ddl-auto:update가 테이블을 만든다.
#   docker compose -f docker-compose.loadtest.yml up -d mysql app
#   ./loadtest/seed.sh
#
# docker-compose.seed.yml(적재 가속 override)은 1만 건 규모에서는 필요 없다.
# 100만 건 이상 넣을 때만 얹는다.
set -euo pipefail

cd "$(dirname "$0")/.."

TARGET_ROWS="${TARGET_ROWS:-10000}"
BATCH="${BATCH:-20000}"
# 목표치가 배치보다 작으면 배치를 줄인다. 그대로 두면 목표를 넘겨 적재한다.
[ "$BATCH" -le "$TARGET_ROWS" ] || BATCH="$TARGET_ROWS"
COMPOSE="${COMPOSE:-docker compose -f docker-compose.loadtest.yml}"
BASE="${BASE:-http://localhost:8080/api}"
# charset을 지정하지 않으면 클라이언트가 latin1로 붙어 멀티바이트 문자 길이가 잘못 계산된다.
MYSQL="$COMPOSE exec -T mysql mysql -uroot -proot --database=loadtest_db --default-character-set=utf8mb4 --skip-column-names --silent"

echo "[1/4] 테이블 존재 확인"
if ! $MYSQL -e "select 1 from article limit 1" >/dev/null 2>&1; then
  echo "  article 테이블 없음. app을 먼저 기동해 스키마를 생성해라."
  echo "  $COMPOSE up -d mysql app"
  exit 1
fi

echo "[2/4] 시드용 회원 확보"
# 게시글은 member_id NOT NULL이다. 비밀번호 해시를 직접 만들지 않으려고 sign-up API를 쓴다.
MEMBER_ID="$($MYSQL -e "select member_id from member order by member_id limit 1" || true)"
if [ -z "$MEMBER_ID" ]; then
  SUFFIX="$(date +%s)"
  PASSWORD='JadeHello1234!'
  curl -s -o /dev/null -X POST "$BASE/auth/sign-up" -H 'Content-Type: application/json' \
    -d "{\"email\":\"seed${SUFFIX}@kakaotech.com\",\"password\":\"$PASSWORD\",\"checkPassword\":\"$PASSWORD\",\"nickname\":\"seed${SUFFIX: -5}\",\"profileImageUrl\":\"https://example.invalid/profile/loadtest.webp\"}"
  MEMBER_ID="$($MYSQL -e "select member_id from member order by member_id limit 1")"
fi
[ -n "$MEMBER_ID" ] || { echo "  회원 생성 실패"; exit 1; }
echo "  member_id=$MEMBER_ID"

current_rows() { $MYSQL -e "select count(*) from article"; }

ROWS="$(current_rows)"
echo "[3/4] 현재 ${ROWS}건 -> 목표 ${TARGET_ROWS}건"

if [ "$ROWS" -ge "$TARGET_ROWS" ]; then
  echo "  이미 목표 달성. 종료."
  exit 0
fi

# 컬럼명은 Hibernate가 생성한 실제 이름을 따른다. optionA -> optiona (option_a 아님).
# created_at은 1년 범위로 흩어 커서 페이징에서 동일 시각 tie가 생기지 않게 한다.
INSERT_COLS="article_uuid, title, optiona, optionb, liked_count, member_id, created_at, updated_at"
RANDOM_ROW="
  replace(uuid(), '-', ''),
  concat('loadtest ', floor(rand() * 1000000)),
  concat('optA', floor(rand() * 10000)),
  concat('optB', floor(rand() * 10000)),
  floor(rand() * 500),
  ${MEMBER_ID},
  now(6) - interval floor(rand() * 31536000) second,
  now(6)
"

if [ "$ROWS" -eq 0 ]; then
  $MYSQL -e "insert into article ($INSERT_COLS) select $RANDOM_ROW"
  ROWS=1
fi

echo "  1단계: 배가법으로 ${BATCH}건까지"
while [ "$ROWS" -lt "$BATCH" ]; do
  # 목표를 넘기지 않도록 남은 만큼만 복제한다.
  STEP=$((BATCH - ROWS))
  [ "$STEP" -le "$ROWS" ] || STEP="$ROWS"
  $MYSQL -e "insert into article ($INSERT_COLS) select $RANDOM_ROW from article limit $STEP"
  ROWS="$(current_rows)"
  echo "    ${ROWS}건"
done

echo "  2단계: ${BATCH}건씩 복제"
START_TS=$(date +%s)
while [ "$ROWS" -lt "$TARGET_ROWS" ]; do
  REMAIN=$((TARGET_ROWS - ROWS))
  LIMIT=$((REMAIN < BATCH ? REMAIN : BATCH))
  $MYSQL -e "insert into article ($INSERT_COLS) select $RANDOM_ROW from article limit $LIMIT"
  ROWS=$((ROWS + LIMIT))
  if [ $((ROWS % (BATCH * 25))) -lt "$BATCH" ]; then
    ELAPSED=$(( $(date +%s) - START_TS ))
    echo "    ${ROWS}건 / ${TARGET_ROWS}건 (${ELAPSED}초 경과)"
  fi
done

# ArticleSummaryResponse.of가 voteCount를 null 체크 없이 참조한다.
# 투표수 행이 없는 게시글이 목록에 걸리면 조회가 NPE로 실패한다.
echo "[4/4] 투표수 행 보정"
$MYSQL -e "
insert into article_vote_count (article_id, counta, countb)
select a.article_id, floor(rand() * 100), floor(rand() * 100)
  from article a
  left join article_vote_count v on v.article_id = a.article_id
 where v.article_id is null
"
MISSING="$($MYSQL -e "select count(*) from article a left join article_vote_count v on v.article_id = a.article_id where v.article_id is null")"
echo "  투표수 누락: ${MISSING}건"

echo "적재 완료: $(current_rows)건"
$MYSQL -e "select count(*) as total, min(created_at) as oldest, max(created_at) as newest from article" || true
