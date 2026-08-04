#!/usr/bin/env python3
"""JIT 컴파일 로그(-XX:+LogCompilation)에서 티어별 컴파일 건수를 센다.

    python3 loadtest/analyze-jit.py loadtest/results/jit/*.log

로그는 컴파일이 끝난 메서드마다 아래 형태의 줄을 남긴다.

    <nmethod compile_id='...' level='4' method='...' .../>

level이 그 메서드가 도달한 티어다.

    0        인터프리터
    1, 2, 3  C1  (2는 프로파일링 미수집, 3은 수집)
    4        C2

L2가 많다는 것은 C2 큐가 밀려 프로파일링 수집을 포기했다는 신호다.
"""
import collections
import re
import sys

APP_PACKAGE = "kakaotech"
NMETHOD = re.compile(r"<nmethod[^>]*?level='(\d+)'[^>]*?method='([^']+)'")


def analyze(path):
    with open(path, errors="ignore") as f:
        entries = NMETHOD.findall(f.read())

    total = collections.Counter(level for level, _ in entries)
    app = collections.Counter(
        level for level, method in entries if method.startswith(APP_PACKAGE)
    )
    app_c2 = sorted(
        {
            method.split()[0]
            for level, method in entries
            if level == "4" and method.startswith(APP_PACKAGE)
        }
    )

    count = sum(total.values())
    print(f"=== {path}")
    print(f"  전체 {count}건  " + "  ".join(f"L{lv}={total[lv]}" for lv in "01234"))
    if count:
        print(f"  L2 비율 {100 * total['2'] / count:.1f}%")
    print("  앱 코드  " + "  ".join(f"L{lv}={app[lv]}" for lv in "01234"))
    print(f"  앱 코드 C2 도달 {len(app_c2)}개")
    for method in app_c2:
        print("    - " + method.replace("kakaotech.task4.", ""))
    print()


if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    for path in sys.argv[1:]:
        analyze(path)
