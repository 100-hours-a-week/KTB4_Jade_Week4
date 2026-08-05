"""블루-그린 전환 실험용 Locust 시나리오.

트래픽 모델은 기존 locustfile.py와 같다. 차이는 전환 marker 파일을 읽어 요청 이름을
전환 기준 상대 구간으로 나누는 점이다.
"""
import json
import os
import random
import time
from pathlib import Path

import requests
from locust import HttpUser, between, events, task

PASSWORD = "JadeHello1234!"

_access_token = None
_article_uuids = []
_marker_path = Path(os.getenv("SWITCH_MARKER_FILE", "/mnt/locust/results/blue-green-switch/switch.marker"))
_switch_epoch = None
_last_marker_mtime = 0.0


def _issue_token(host: str) -> str:
    suffix = int(time.time())
    headers = {"Content-Type": "application/json"}
    email = f"bg{suffix}@kakaotech.com"

    requests.post(
        f"{host}/auth/sign-up",
        headers=headers,
        data=json.dumps({
            "email": email,
            "password": PASSWORD,
            "checkPassword": PASSWORD,
            "nickname": f"bg{suffix % 100000}",
            "profileImageUrl": "https://example.invalid/profile/loadtest.webp",
        }),
        timeout=30,
    )

    response = requests.post(
        f"{host}/auth/sign-in",
        headers=headers,
        data=json.dumps({"email": email, "password": PASSWORD}),
        timeout=30,
    )

    token = response.cookies.get("access_token")
    if not token:
        raise RuntimeError(
            f"access_token 쿠키 없음. status={response.status_code} body={response.text[:200]}"
        )
    return token


def _fetch_article_uuids(host: str, token: str) -> list:
    response = requests.get(
        f"{host}/articles?size=10",
        cookies={"access_token": token},
        timeout=30,
    )
    if response.status_code != 200:
        raise RuntimeError(f"목록 조회 실패. status={response.status_code} body={response.text[:200]}")

    articles = response.json()["data"]["articles"]
    return [article["articleUuid"] for article in articles]


@events.test_start.add_listener
def on_test_start(environment, **_kwargs):
    global _access_token, _article_uuids
    host = (environment.host or os.getenv("LOCUST_HOST", "http://nginx/api")).rstrip("/")
    _access_token = _issue_token(host)
    _article_uuids = _fetch_article_uuids(host, _access_token)


def _read_switch_epoch():
    global _switch_epoch, _last_marker_mtime
    try:
        stat = _marker_path.stat()
    except FileNotFoundError:
        return None

    if _switch_epoch is not None and stat.st_mtime <= _last_marker_mtime:
        return _switch_epoch

    try:
        _switch_epoch = float(_marker_path.read_text().strip())
        _last_marker_mtime = stat.st_mtime
    except ValueError:
        return None
    return _switch_epoch


def _bucket() -> str:
    switch_epoch = _read_switch_epoch()
    if switch_epoch is None:
        return "pre-switch"

    elapsed = time.time() - switch_epoch
    if elapsed < 0:
        return "pre-switch"
    if elapsed <= 30:
        return "0-30s"
    if elapsed <= 60:
        return "30-60s"
    return "60s+"


class ArticleUser(HttpUser):
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

