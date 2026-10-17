#!/usr/bin/env python3
"""Достижения: дерево зарегистрировано на сервере (через /advancements).

Usage: python3 tools/tests_advancements.py --base http://127.0.0.1:9878
"""

import sys

import irbridge_testkit as t
from irbridge_testkit import check

EXPECTED = [
    "root", "machine", "steel", "circuit", "advanced_circuit", "generator",
    "wire", "batbox", "heat", "tank", "rotor", "kinetic", "reactor",
    "teleporter", "matter",
]


def main():
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]

    body = t.call("/advancements")
    if not body.get("server", False):
        check("сервер запущен", False, body)
        return t.summary("tests_advancements")

    ids = {entry.split(":", 1)[1] for entry in body.get("advancements", [])}
    missing = sorted(set(EXPECTED) - ids)
    extra = sorted(ids - set(EXPECTED))

    check("все достижения мода зарегистрированы", not missing, f"нет: {missing}")
    check("лишних достижений нет", not extra, f"лишние: {extra}")
    check("всего достижений: 15", body.get("count") == len(EXPECTED), f"count={body.get('count')}")

    return t.summary("tests_advancements")


if __name__ == "__main__":
    raise SystemExit(main())
