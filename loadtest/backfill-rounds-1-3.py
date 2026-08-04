#!/usr/bin/env python3
"""1~3회차 결과를 runs.jsonl로 복원한다.

1~3회차는 warmup-target-experiment.sh가 생기기 전에 run-scenario.sh로 돌렸다.
파일 이름에 조건(A/B/C)이 없고 워밍업 횟수만 있어서, 당시 기록해 둔 p95 값으로
파일을 식별해 조건·회차를 붙인다. 재측정 대신 이 매핑을 쓰는 이유는 원본 CSV가
그대로 남아 있어 p50/p99/max까지 복원 가능하기 때문이다.
"""
import csv
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RESULTS = ROOT / "loadtest/results"
OUT = ROOT / "loadtest/results/experiment"

# (조건, 회차, stamp, 워밍업 소요초). 소요초는 당시 로그 기준, 미기록은 0.
MAPPING = [
    ("A", 1, "20260803-230340-warmup0", 0),
    ("A", 2, "20260803-231639-warmup0", 0),
    ("A", 3, "20260803-233637-warmup0", 0),
    ("B", 1, "20260803-230745-warmup500", 23),
    ("B", 2, "20260803-232905-warmup500", 23),
    ("B", 3, "20260803-233245-warmup500", 23),
    ("C", 1, "20260803-231209-warmup500", 40),
    ("C", 2, "20260803-231956-warmup500", 40),
    ("C", 3, "20260803-233958-warmup500", 40),
]

TARGET_ROWS = {
    "GET /articles [0-30s]": "list",
    "GET /articles/{uuid} [0-30s]": "detail",
}


def parse(stamp: str) -> dict:
    metrics = {}
    with (RESULTS / f"{stamp}_stats.csv").open() as f:
        for row in csv.DictReader(f):
            key = TARGET_ROWS.get(row["Name"])
            if not key:
                continue
            metrics[key] = {
                "requests": int(float(row["Request Count"])),
                "p50": round(float(row["50%"])),
                "p95": round(float(row["95%"])),
                "p99": round(float(row["99%"])),
                "max": round(float(row["Max Response Time"])),
            }
    return metrics


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    lines = []
    for cond, rnd, stamp, warmup in MAPPING:
        lines.append(json.dumps({
            "condition": cond,
            "round": rnd,
            "stamp": stamp,
            "boot_seconds": 0,
            "warmup_seconds": warmup,
            "metrics": parse(stamp),
            "cpu": {},
            "source": "backfill",
        }, ensure_ascii=False))
    (OUT / "runs.jsonl").write_text("\n".join(lines) + "\n")
    print(f"{len(lines)}건 복원 -> {OUT / 'runs.jsonl'}")


if __name__ == "__main__":
    main()
