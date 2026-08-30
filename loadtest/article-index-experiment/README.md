# 게시글 목록 인덱스 수동 실험 가이드

이 디렉터리는 게시글 1천만 건에서 다음 세 변수를 직접 분리해 확인하기 위한 실험 키트다.

- 복합 인덱스 순서
- `deleted_at` 필터의 처리 위치
- INNER JOIN과 LEFT JOIN의 조인 순서

스크립트가 실험을 자동으로 돌리지 않는다. 각 단계에서 실행 계획을 읽고 다음 단계로
넘어갈 수 있도록 SQL을 분리했다.

## 0. 고정 조건

- MySQL 8.0
- MySQL 컨테이너 1 CPU / 1 GiB
- InnoDB Buffer Pool 256 MiB
- 게시글 10,000,000건
- 조회 LIMIT 10
- 동일한 데이터와 컨테이너 사용

현재 기준선은 `(created_at, article_id)` 인덱스다. 실험을 중단하더라도
[`indexes/99_restore.sql`](indexes/99_restore.sql)을 실행하면 이 상태로 돌아온다.

## 1. MySQL 접속

```bash
docker exec -it loadtest-mysql \
  mysql -uroot -proot --database=loadtest_db --default-character-set=utf8mb4
```

한 번 접속한 세션에서 상태 카운터와 쿼리를 함께 실행해야 `Handler_read%`를 비교할 수
있다.

## 2. 실험 전 기준선

[`00_baseline.sql`](00_baseline.sql)을 실행하고 다음을 결과 기록지에 옮긴다.

```bash
docker exec -i loadtest-mysql \
  mysql -uroot -proot --database=loadtest_db --default-character-set=utf8mb4 \
  < loadtest/article-index-experiment/00_baseline.sql
```

- 전체 게시글 수와 활성 게시글 수
- `member_id IS NULL` 건수
- 현재 인덱스 컬럼 순서
- MySQL 버전과 Buffer Pool 크기
- 최소·최대 생성 시각

게시글 수가 1천만 건이 아니거나 `member_id IS NULL`이 존재하면 실험을 진행하지 않는다.

## 3. 인덱스 대조군

아래 파일을 **한 번에 하나만** 실행한다.

1. A: [`indexes/01_variant_a.sql`](indexes/01_variant_a.sql) — `(article_id, created_at)`
2. B: [`indexes/02_variant_b.sql`](indexes/02_variant_b.sql) — `(created_at, article_id)`
3. C: [`indexes/03_variant_c.sql`](indexes/03_variant_c.sql) — `(deleted_at, created_at, article_id)`

각 파일은 변경 전후 인덱스와 게시글 수를 출력하고 마지막에 `ANALYZE TABLE`을 실행한다.
적용이 끝나면 반드시 `SHOW INDEX` 결과에서 컬럼 순서를 직접 확인한다.

예를 들어 A를 적용하는 명령은 다음과 같다.

```bash
docker exec -i loadtest-mysql \
  mysql -uroot -proot --database=loadtest_db --default-character-set=utf8mb4 \
  < loadtest/article-index-experiment/indexes/01_variant_a.sql
```

게시글 1천만 건의 보조 인덱스 생성은 수분이 걸리고 추가 디스크 공간을 사용할 수 있다.
실행 전 `df -h`로 여유 공간을 확인하고, `ALTER TABLE` 중에는 컨테이너를 중단하지 않는다.

## 4. 쿼리별 관찰 순서

인덱스 하나를 적용한 상태에서 [`queries.sql`](queries.sql)의 블록을 다음 순서로 하나씩
실행한다.

1. 첫 페이지 INNER JOIN
2. 첫 페이지 LEFT JOIN
3. 중간 커서 INNER JOIN
4. 중간 커서 LEFT JOIN
5. 후반 커서 INNER JOIN
6. 후반 커서 LEFT JOIN

각 블록에서 먼저 일반 `EXPLAIN`의 다음 컬럼을 읽는다.

- `table`: 어떤 테이블부터 읽는가
- `type`: ALL, index, range, ref 중 무엇인가
- `key`: `idx_article_created_at`을 선택했는가
- `rows`, `filtered`: 예상치
- `Extra`: `Using filesort`, `Using where`, `Backward index scan` 여부

그 다음 `EXPLAIN ANALYZE`에서 실제 처리 행 수, loops, root의 actual time을 기록한다.

## 5. Warm 측정

하나의 쿼리 블록에 대해 다음 순서를 지킨다.

1. 결과 SELECT를 3회 실행해 예열한다.
2. `EXPLAIN ANALYZE`를 7회 실행한다.
3. root 노드의 종료 시간과 실제 처리 행 수를 각각 기록한다.
4. 7개 시간의 중앙값과 p95는 결과를 모은 뒤 계산한다.

7회에서는 시간을 오름차순으로 정렬했을 때 4번째 값이 중앙값이고, nearest-rank 방식의
p95는 7번째 값인 최댓값이다.

`EXPLAIN ANALYZE` 자체의 계측 비용이 있으므로 API 응답시간과 같은 값으로 해석하지
않는다. 여기서는 조건 간 실행 계획과 상대적인 반복 결과를 비교한다.

## 6. Handler 카운터

같은 MySQL 세션에서 쿼리 하나를 다음처럼 감싼다.

```sql
FLUSH STATUS;

-- 여기에 queries.sql의 실제 SELECT 한 개를 실행

SHOW SESSION STATUS
WHERE Variable_name LIKE 'Handler_read%';
```

특히 다음 값을 기록한다.

- `Handler_read_key`: 인덱스 키를 이용한 탐색
- `Handler_read_next`, `Handler_read_prev`: 인덱스 순차/역순 탐색
- `Handler_read_rnd_next`: 테이블 또는 임시 결과의 순차 읽기

이 값은 세션 단위 증분이다. 다른 세션의 쿼리와 섞이지 않도록 실험용 터미널 하나만
사용한다.

## 7. Cold 관찰

Cold 결과는 인덱스별로 대표 쿼리인 첫 페이지 INNER/LEFT JOIN만 1회씩 관찰한다.

```bash
docker restart loadtest-mysql
docker exec loadtest-mysql mysqladmin ping -h 127.0.0.1 -uroot -proot
```

재접속한 뒤 쿼리 전후에 다음 값을 조회한다.

```sql
SHOW GLOBAL STATUS
WHERE Variable_name IN (
  'Innodb_buffer_pool_read_requests',
  'Innodb_buffer_pool_reads'
);
```

- `read_requests`: Buffer Pool을 통한 논리 읽기
- `reads`: 디스크에서 Buffer Pool로 가져온 물리 읽기

Cold 1회는 캐시 영향을 설명하는 보조 자료다. Warm 7회 결과와 직접 섞어 개선 배수를
계산하지 않는다.

## 8. 결과 판정

다음을 모두 만족하는 후보만 코드 반영 대상으로 둔다.

- 첫 페이지와 커서 페이지의 결과가 기준 쿼리와 동일함
- `idx_article_created_at` 선택
- `Using filesort` 제거
- LIMIT 10에 가까운 게시글만 접근
- 1천만 건 전체 순회 제거
- Warm 7회에서도 개선이 반복됨

C 인덱스는 현재 데이터의 `deleted_at`이 모두 NULL이므로 실제 측정 결과 없이 더 좋다고
판단하지 않는다. B와 C가 비슷하면 컬럼 수와 유지 비용이 더 작은 B를 선택한다.

## 9. 종료와 복구

실험을 마치거나 중간에 중단하면 다음 파일을 실행한다.

```bash
docker exec -i loadtest-mysql \
  mysql -uroot -proot --database=loadtest_db --default-character-set=utf8mb4 \
  < loadtest/article-index-experiment/indexes/99_restore.sql
```

복구 후 게시글 수가 기준선과 같고 인덱스가 `(created_at, article_id)`인지 확인한다.
실험 결과가 확정되기 전에는 Repository 쿼리나 Flyway 마이그레이션을 변경하지 않는다.
