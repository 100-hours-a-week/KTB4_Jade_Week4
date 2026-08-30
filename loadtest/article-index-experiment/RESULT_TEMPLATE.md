# 게시글 목록 인덱스 수동 실험 결과

## 기준선

| 항목 | 값 |
| --- | --- |
| 측정일 | |
| MySQL 버전 | |
| 게시글 수 | |
| 활성 게시글 수 | |
| NULL member 수 | |
| Buffer Pool | |
| 데이터 생성 시각 범위 | |

## 실행 계획 결과

| 인덱스 | 쿼리 | 선행 테이블 | type | key | 예상 rows | 실제 article rows | filesort | root actual time |
| --- | --- | --- | --- | --- | ---: | ---: | --- | ---: |
| A `(article_id, created_at)` | 첫 페이지 INNER | | | | | | | |
| A | 첫 페이지 LEFT | | | | | | | |
| A | 중간 커서 INNER | | | | | | | |
| A | 중간 커서 LEFT | | | | | | | |
| A | 후반 커서 INNER | | | | | | | |
| A | 후반 커서 LEFT | | | | | | | |
| B `(created_at, article_id)` | 첫 페이지 INNER | | | | | | | |
| B | 첫 페이지 LEFT | | | | | | | |
| B | 중간 커서 INNER | | | | | | | |
| B | 중간 커서 LEFT | | | | | | | |
| B | 후반 커서 INNER | | | | | | | |
| B | 후반 커서 LEFT | | | | | | | |
| C `(deleted_at, created_at, article_id)` | 첫 페이지 INNER | | | | | | | |
| C | 첫 페이지 LEFT | | | | | | | |
| C | 중간 커서 INNER | | | | | | | |
| C | 중간 커서 LEFT | | | | | | | |
| C | 후반 커서 INNER | | | | | | | |
| C | 후반 커서 LEFT | | | | | | | |

## Warm 7회

| 인덱스 | 쿼리 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 중앙값 | p95 |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| A | | | | | | | | | | |
| B | | | | | | | | | | |
| C | | | | | | | | | | |

## Handler 카운터

| 인덱스 | 쿼리 | read_key | read_next | read_prev | read_rnd_next |
| --- | --- | ---: | ---: | ---: | ---: |
| A | | | | | |
| B | | | | | |
| C | | | | | |

## Cold Buffer Pool 카운터

| 인덱스 | 쿼리 | read_requests 증가 | reads 증가 | actual time |
| --- | --- | ---: | ---: | ---: |
| A | 첫 페이지 INNER | | | |
| A | 첫 페이지 LEFT | | | |
| B | 첫 페이지 INNER | | | |
| B | 첫 페이지 LEFT | | | |
| C | 첫 페이지 INNER | | | |
| C | 첫 페이지 LEFT | | | |

## 관찰 → 가설 → 검증 → 결론

### 관찰


### 1차 가설


### 1차 검증과 반증


### 2차 가설


### 2차 검증


### 최종 결론


### 측정 한계

