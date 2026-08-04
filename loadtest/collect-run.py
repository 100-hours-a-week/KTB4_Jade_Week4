#!/usr/bin/env python3
"""회차 하나의 측정값을 모아 JSON 한 줄로 출력한다.

locust CSV, docker stats 스냅샷, jcmd 출력이 각각 다른 파일로 흩어져 있어서
회차마다 하나로 합쳐 둔다.
"""
import argparse
import csv
import json
import re
from pathlib import Path

# 0-30s 구간만 본다. 기동 직후 지연이 관심사다.
TARGET_ROWS = {
    "GET /articles [0-30s]": "list",
    "GET /articles/{uuid} [0-30s]": "detail",
}


def parse_stats(path: Path) -> dict:
    if not path.exists():
        return {}

    metrics = {}
    with path.open() as f:
        for row in csv.DictReader(f):
            key = TARGET_ROWS.get(row.get("Name", ""))
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


def parse_stats_snapshot(path: Path) -> dict:
    """docker stats 한 줄씩: '<name> <cpu%> <mem used> / <limit>'"""
    if not path.exists():
        return {}

    result = {}
    for line in path.read_text().splitlines():
        parts = line.split()
        if len(parts) >= 3 and parts[0].startswith("loadtest-"):
            result[parts[0].replace("loadtest-", "")] = {
                "cpu": parts[1],
                "mem": parts[2],
            }
    return result


def parse_queue_total(path: Path) -> int:
    """Compiler.queue 출력에서 대기 중인 컴파일 작업 수를 센다."""
    if not path.exists():
        return 0
    # 각 큐의 항목은 'C1 CompileQueue:' / 'C2 CompileQueue:' 아래에 한 줄씩 나온다.
    return sum(1 for line in path.read_text().splitlines() if re.match(r"^\s*\d+\s+\d+\s", line))


def parse_codecache_used(path: Path) -> int:
    if not path.exists():
        return 0
    match = re.search(r"used=(\d+)Kb", path.read_text())
    return int(match.group(1)) if match else 0


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--condition", required=True)
    parser.add_argument("--round", type=int, required=True)
    parser.add_argument("--stamp", required=True)
    parser.add_argument("--boot-seconds", type=int, default=0)
    parser.add_argument("--warmup-seconds", type=int, default=0)
    parser.add_argument("--out-dir", required=True)
    args = parser.parse_args()

    out = Path(args.out_dir)
    record = {
        "condition": args.condition,
        "round": args.round,
        "stamp": args.stamp,
        "boot_seconds": args.boot_seconds,
        "warmup_seconds": args.warmup_seconds,
        "metrics": parse_stats(out / f"{args.stamp}_stats.csv"),
        "cpu": parse_stats_snapshot(out / f"{args.stamp}-stats.txt"),
        "queue_before_total": parse_queue_total(out / f"{args.stamp}-queue-before.txt"),
        "queue_after_total": parse_queue_total(out / f"{args.stamp}-queue-after.txt"),
        "codecache_before_used_kb": parse_codecache_used(out / f"{args.stamp}-codecache-before.txt"),
        "codecache_after_used_kb": parse_codecache_used(out / f"{args.stamp}-codecache-after.txt"),
    }
    print(json.dumps(record, ensure_ascii=False))


if __name__ == "__main__":
    main()
