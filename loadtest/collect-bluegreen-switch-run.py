#!/usr/bin/env python3
"""블루-그린 전환 회차 하나의 CSV와 메타데이터를 JSON 한 줄로 합친다."""
import argparse
import csv
import json
from pathlib import Path

TARGET_ROWS = {
    "GET /articles [0-30s]": "list_0_30s",
    "GET /articles/{uuid} [0-30s]": "detail_0_30s",
    "GET /articles [30-60s]": "list_30_60s",
    "GET /articles/{uuid} [30-60s]": "detail_30_60s",
    "GET /articles [60s+]": "list_60s_plus",
    "GET /articles/{uuid} [60s+]": "detail_60s_plus",
}


def parse_stats(path: Path) -> dict:
    metrics = {}
    with path.open() as f:
        for row in csv.DictReader(f):
            key = TARGET_ROWS.get(row.get("Name", ""))
            if not key:
                continue
            metrics[key] = {
                "requests": int(float(row["Request Count"])),
                "failures": int(float(row["Failure Count"])),
                "p50": round(float(row["50%"])),
                "p95": round(float(row["95%"])),
                "p99": round(float(row["99%"])),
                "max": round(float(row["Max Response Time"])),
            }
    return metrics


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--meta", required=True)
    parser.add_argument("--out-dir", required=True)
    args = parser.parse_args()

    meta_path = Path(args.meta)
    out_dir = Path(args.out_dir)
    meta = json.loads(meta_path.read_text())
    stamp = meta["stamp"]
    meta["metrics"] = parse_stats(out_dir / f"{stamp}_stats.csv")
    print(json.dumps(meta, ensure_ascii=False))


if __name__ == "__main__":
    main()

