#!/usr/bin/env python3
"""예비 측정 3회를 runs.jsonl로 복원한다. 이미 한 번 실행했으므로 다시 쓸 일이 없다.

예비 측정은 warmup-target-experiment.sh가 생기기 전에 run-scenario.sh로 돌렸다.
파일 이름에 조건(A/B/C)이 없고 워밍업 횟수만 있어서, 당시 기록해 둔 p95 값으로
파일을 식별해 조건·회차를 붙인다. 재측정 대신 이 매핑을 쓰는 이유는 원본 CSV가
그대로 남아 있어 p50/p99/max까지 복원 가능하기 때문이다.

주의: 이 스크립트는 runs.jsonl을 통째로 새로 쓴다. 본 측정 21회가 들어 있는 상태에서
실행하면 그 기록이 사라지므로, 파일이 이미 있으면 거부한다.
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
    target = OUT / "runs.jsonl"

    # 덮어쓰면 본 측정 기록이 사라진다. 되돌릴 방법이 없으므로 여기서 멈춘다.
    if target.exists():
        raise SystemExit(
            f"{target} 이(가) 이미 있다. 이 스크립트는 파일을 통째로 새로 쓰므로 "
            "본 측정 기록이 사라진다. 정말 다시 만들려면 기존 파일을 옮긴 뒤 실행하고, "
            "본 측정은 warmup-target-experiment.sh로 다시 채워야 한다."
        )

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
            # 오염된 예비 측정이다. 이 값이 없으면 analyze-experiment.py가 본 측정으로 센다.
            "phase": "pilot",
        }, ensure_ascii=False))
    target.write_text("\n".join(lines) + "\n")
    print(f"{len(lines)}건 복원 -> {target}")


if __name__ == "__main__":
    main()
