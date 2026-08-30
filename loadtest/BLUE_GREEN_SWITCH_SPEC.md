# 로컬 블루-그린 전환 A/B 테스트

## 목적

Locust가 계속 요청을 보내는 상태에서 active 백엔드를 `app-blue`에서 `app-green`으로
전환하고, nginx reload 완료 후 switch marker를 기록한 시점부터 0-30초 구간의 p95와
p99를 비교한다. reload 완료 직후 marker 기록 전까지의 짧은 요청은 `pre-switch`로
분류된다.

- A: `app-green` health check 통과 후 즉시 nginx upstream 전환
- B: `app-green` health check 통과 후 내부 워밍업을 완료하고 nginx upstream 전환

운영 CD는 이미 비활성 색상을 기동하고, 헬스체크와 워밍업을 거친 뒤 nginx upstream을
교체한다. 이 실험은 그 순서에서 워밍업 유무만 분리해 로컬에서 비교한다.

## 로컬 구조

- `docker-compose.bluegreen-loadtest.yml`
  - `mysql`
  - `app-blue`
  - `app-green`
  - `nginx`
  - `locust`
- Locust target은 `http://nginx/api`다.
- 호스트에서 직접 확인할 때는 `http://localhost:8090/api/auth/csrf`를 쓴다.
- nginx는 운영처럼 `/api/internal/`을 404로 막는다. 워밍업은 `app-green` 컨테이너 안에서
  `http://localhost:8080/api/internal/warmup`으로 호출한다.
- 실험 중 바뀌는 nginx 설정은 `loadtest/results/blue-green-switch/nginx/conf.d`에 생성된다.

## 준비

기존 loadtest DB 볼륨을 공유한다. 처음 한 번은 스키마 생성과 시드 적재가 필요하다.

```bash
docker compose -f docker-compose.loadtest.yml up -d --build mysql app
./loadtest/seed.sh
docker compose -f docker-compose.loadtest.yml rm -sf app
```

시드가 이미 있다면 다시 넣지 않아도 된다.

## 실행

조건별 7회 측정:

```bash
./loadtest/blue-green-switch-experiment.sh 1 7
```

부하 강도와 전환 후 관찰 시간을 바꿀 수 있다.

```bash
VUS=10 SPAWN_RATE=5 POST_SWITCH_SECONDS=90 ./loadtest/blue-green-switch-experiment.sh 1 7
```

중간에 끊긴 경우 남은 회차만 지정한다.

```bash
PLAN="B:4 A:4 B:5" ./loadtest/blue-green-switch-experiment.sh
```

## 결과 확인

```bash
python3 loadtest/analyze-bluegreen-switch.py loadtest/results/blue-green-switch/runs.jsonl
```

주 지표:

- `list_0_30s`의 `p95`, `p99`
- `detail_0_30s`의 `p95`, `p99`
- failure count

평균 하나만 보지 말고 중앙값, 표준편차, 회차별 원값을 같이 본다. 꼬리 latency는 회차
편차가 커서 단일 회차로 결론을 내리면 안 된다.

## 실행 결과 (2026-08-04)

A/B 각각 7회, 총 14회를 실행했으며 회차당 Locust 실행시간은 4분이었다. 부하 시간만
56분이고 컨테이너 재생성 등의 준비 시간을 포함해 전체 본 측정은 약 1시간 걸렸다.

| API | 지표 | A 중앙값 | B 중앙값 | B 개선율 |
| --- | --- | --- | --- | --- |
| 목록 | p95 | 200 ms | 58 ms | 71% 감소 |
| 목록 | p99 | 400 ms | 120 ms | 70% 감소 |
| 상세 | p95 | 170 ms | 39 ms | 77% 감소 |
| 상세 | p99 | 530 ms | 81 ms | 85% 감소 |

두 조건 모두 요청 실패는 0건이었다.

## 해석 기준

- B의 0-30초 p95/p99 중앙값이 A보다 낮고 failure count가 늘지 않으면, 전환 전 워밍업이
  사용자에게 노출되는 초기 지연을 줄인 것으로 판단한다.
- A/B 차이가 작으면 기존 [결과 보고서](https://app.notion.com/p/3ccc255d936b80698a60d3974c410621)의 “cold JVM 직후 부하” 결과와 달리
  nginx reload 전환 자체의 영향이 작거나, Locust 부하가 부족했을 수 있다.
- 로컬 수치는 운영과 직접 동일하지 않다. 운영은 t3.micro 한 대에서 nginx, frontend,
  app-blue, app-green, mysql이 같이 돈다.
