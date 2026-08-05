#!/usr/bin/env python3
"""블루-그린 전환 직후 latency를 조건별로 집계한다."""
import json
import statistics
import sys
from pathlib import Path

METRICS = ["p95", "p99", "max"]
TARGETS = [
    ("list_0_30s", "목록 0-30s"),
    ("detail_0_30s", "상세 0-30s"),
]
CONDITIONS = [("A", "헬스체크 후 즉시 전환"), ("B", "헬스체크 후 워밍업 후 전환")]


def load(path: Path) -> list:
    return [json.loads(line) for line in path.read_text().splitlines() if line.strip()]


def values(runs: list, condition: str, target: str, metric: str) -> list:
    result = []
    for run in sorted(runs, key=lambda r: r["round"]):
        if run["condition"] != condition:
            continue
        row = run.get("metrics", {}).get(target)
        if row:
            result.append(row[metric])
    return result


def summarize(vals: list) -> tuple:
    if not vals:
        return 0.0, 0.0, 0.0
    sd = statistics.stdev(vals) if len(vals) > 1 else 0.0
    return statistics.mean(vals), statistics.median(vals), sd


def counts(runs: list, condition: str, target: str) -> tuple:
    requests = 0
    failures = 0
    for run in runs:
        if run["condition"] != condition:
            continue
        row = run.get("metrics", {}).get(target)
        if row:
            requests += row["requests"]
            failures += row["failures"]
    return requests, failures


def main():
    runs = load(Path(sys.argv[1]))

    for target, label in TARGETS:
        print(f"\n### {label}\n")
        for metric in METRICS:
            print(f"[{metric}]")
            print(f"{'조건':<28}{'회차별':<36}{'평균':>8}{'중앙값':>9}{'표준편차':>10}")
            for condition, condition_label in CONDITIONS:
                vals = values(runs, condition, target, metric)
                mean, median, sd = summarize(vals)
                series = ", ".join(str(v) for v in vals)
                print(f"{condition + ' ' + condition_label:<28}{series:<36}{mean:>8.1f}{median:>9.1f}{sd:>10.1f}")
            print()

        print("[requests/failures]")
        for condition, condition_label in CONDITIONS:
            requests, failures = counts(runs, condition, target)
            print(f"{condition} {condition_label}: requests={requests}, failures={failures}")
        print()

    print("\n### 회차 메타\n")
    for condition, condition_label in CONDITIONS:
        selected = [r for r in runs if r["condition"] == condition]
        warmups = [r["warmup_seconds"] for r in selected]
        boots = [r["green_boot_seconds"] for r in selected]
        print(f"{condition} {condition_label}: {len(selected)}회")
        if selected:
            print(f"  green boot 평균 {statistics.mean(boots):.1f}초, warmup 평균 {statistics.mean(warmups):.1f}초")


if __name__ == "__main__":
    main()
