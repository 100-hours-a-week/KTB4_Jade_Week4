#!/usr/bin/env python3
"""runs.jsonl을 조건별로 집계한다.

꼬리 지표는 회차 편차가 커서 평균 하나로는 판단이 어렵다. 평균·중앙값·표준편차를
같이 내고 회차별 원값도 함께 찍는다. 표준편차는 표본표준편차(n-1)다.
"""
import json
import statistics
import sys
from pathlib import Path

METRICS = ["p50", "p95", "p99", "max"]
ENDPOINTS = [("list", "목록"), ("detail", "상세")]
CONDITIONS = [("A", "워밍업 없음"), ("B", "목록만"), ("C", "목록+상세")]


def load(path: Path) -> list:
    """본 측정(main)만 읽는다. pilot 3회는 호스트 경합으로 오염돼 집계에서 제외한다."""
    runs = [json.loads(line) for line in path.read_text().splitlines() if line.strip()]
    return [r for r in runs if r.get("phase", "main") == "main"]


def main():
    path = Path(sys.argv[1])
    runs = load(path)

    for ep, ep_label in ENDPOINTS:
        print(f"\n### {ep_label} 조회 (0-30s)\n")
        for metric in METRICS:
            print(f"[{metric}]")
            print(f"{'조건':<14}{'회차별':<40}{'평균':>8}{'중앙값':>9}{'표준편차':>10}")
            for cond, cond_label in CONDITIONS:
                vals = [
                    r["metrics"][ep][metric]
                    for r in sorted(runs, key=lambda x: x["round"])
                    if r["condition"] == cond and ep in r["metrics"]
                ]
                if not vals:
                    continue
                sd = statistics.stdev(vals) if len(vals) > 1 else 0.0
                series = ", ".join(str(v) for v in vals)
                print(f"{cond + ' ' + cond_label:<14}{series:<40}"
                      f"{statistics.mean(vals):>8.1f}{statistics.median(vals):>9.1f}{sd:>10.1f}")
            print()

    print("\n### 워밍업 소요 시간\n")
    for cond, cond_label in CONDITIONS:
        vals = [r["warmup_seconds"] for r in runs if r["condition"] == cond]
        if vals:
            print(f"{cond} {cond_label}: {vals} 평균 {statistics.mean(vals):.1f}초")

    print("\n### 회차 수")
    for cond, _ in CONDITIONS:
        print(f"  {cond}: {sum(1 for r in runs if r['condition'] == cond)}회")


if __name__ == "__main__":
    main()
