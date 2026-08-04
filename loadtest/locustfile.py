"""부하 대상: 게시글 목록 조회와 상세 조회

실제 사용자는 목록을 보고 그중 하나를 눌러 들어간다. 그 비율을 3:1로 잡았다.
두 API는 공통 경로(필터, JWT, Hibernate, Jackson)를 공유하고 고유 경로만 다르다.
워밍업 대상을 목록만으로 두었을 때 상세가 느린지 보려면 둘 다 부하를 걸어야 한다.

로그인은 측정 대상이 아니다. 테스트 시작 시 계정 하나를 만들어 access_token을 확보하고,
모든 사용자가 그 쿠키를 재사용한다. 매 요청마다 로그인하면 BCrypt 해싱 비용이 섞인다.
"""
import json
import os
import random
import time

import requests
from locust import HttpUser, between, events, task

PASSWORD = "JadeHello1234!"

# 테스트 시작 시각. 기동 직후 구간을 따로 보기 위해 경과 시간을 응답 이름에 붙인다.
_started_at = 0.0
_access_token = None
_article_uuids = []


def _issue_token(host: str) -> str:
    suffix = int(time.time())
    headers = {"Content-Type": "application/json"}

    requests.post(
        f"{host}/auth/sign-up",
        headers=headers,
        data=json.dumps({
            "email": f"locust{suffix}@kakaotech.com",
            "password": PASSWORD,
            "checkPassword": PASSWORD,
            "nickname": f"lc{suffix % 100000}",
            "profileImageUrl": "https://example.invalid/profile/loadtest.webp",
        }),
        timeout=30,
    )

    response = requests.post(
        f"{host}/auth/sign-in",
        headers=headers,
        data=json.dumps({"email": f"locust{suffix}@kakaotech.com", "password": PASSWORD}),
        timeout=30,
    )

    token = response.cookies.get("access_token")
    if not token:
        raise RuntimeError(
            f"access_token 쿠키 없음. status={response.status_code} body={response.text[:200]}"
        )
    return token


def _fetch_article_uuids(host: str, token: str) -> list:
    """상세 조회에 쓸 UUID를 확보한다. 목록 한 번이면 충분하다."""
    response = requests.get(
        f"{host}/articles?size=10",
        cookies={"access_token": token},
        timeout=30,
    )
    if response.status_code != 200:
        raise RuntimeError(f"목록 조회 실패. status={response.status_code}")

    articles = response.json()["data"]["articles"]
    return [article["articleUuid"] for article in articles]


@events.test_start.add_listener
def on_test_start(environment, **_kwargs):
    global _started_at, _access_token, _article_uuids
    _started_at = time.time()
    host = (environment.host or os.getenv("LOCUST_HOST", "http://app:8080/api")).rstrip("/")
    _access_token = _issue_token(host)
    _article_uuids = _fetch_article_uuids(host, _access_token)


def _bucket() -> str:
    """경과 시간 구간. 통계를 구간별로 분리해 초기 지연을 따로 본다."""
    elapsed = time.time() - _started_at
    if elapsed <= 30:
        return "0-30s"
    if elapsed <= 60:
        return "30-60s"
    return "60s+"


class ArticleUser(HttpUser):
    # 서버가 감당하는 만큼만 보내되, 클라이언트가 폭주하지 않게 최소 간격을 둔다.
    wait_time = between(0.1, 0.3)

    def on_start(self):
        self.client.cookies.set("access_token", _access_token)

    @task(3)
    def get_article_list(self):
        self.client.get(
            "/articles?size=10",
            name=f"GET /articles [{_bucket()}]",
        )

    @task(1)
    def get_article_detail(self):
        uuid = random.choice(_article_uuids)
        self.client.get(
            f"/articles/{uuid}",
            name=f"GET /articles/{{uuid}} [{_bucket()}]",
        )
