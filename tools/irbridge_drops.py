#!/usr/bin/env python3
"""Drop tests through the dev bridge.

Pickaxe (or anything not a wrench) must drop the machine casing, a wrench must
drop the machine itself; the contents always drop.  Needs the bridge endpoints
POST /break and GET /entities.

Usage: tools/irbridge_drops.py [--base http://127.0.0.1:9878]
"""

import json
import os
import sys
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:9878"
X0, Z0, Y = 400, -600, 100

PICKAXE = "minecraft:diamond_pickaxe"
WRENCH = "industrialreforged:wrench"

# machine, casing dropped with a pickaxe, contents item to put inside
CASES = [
    ("macerator_1_level", "industrialreforged:machine", "minecraft:iron_ore"),
    ("compressor_1_level", "industrialreforged:machine", "minecraft:iron_ingot"),
    ("generator", "industrialreforged:generator", "minecraft:coal"),
    ("batbox", "industrialreforged:machine", "industrialreforged:re_battery"),
    ("solar_distiller", "industrialreforged:machine", "minecraft:water_bucket"),
]


sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402


def call(path, payload=None, timeout=120):
    # BASE может поменять __main__ (--base), поэтому настраиваем мост при каждом вызове.
    bridgekit.configure(BASE)
    return bridgekit.call(path, payload, timeout)


def execs(*commands):
    return call("/exec", {"commands": list(commands)})


def entities(x, y, z):
    body = call(f"/entities?x={x}&y={y}&z={z}&r=6")
    counts = {}
    for entry in body.get("entities", []):
        if entry.get("item"):
            counts[entry["item"]] = counts.get(entry["item"], 0) + entry.get("count", 1)
    return counts


def run_case(index, machine, casing, content, tool, expect_machine_item):
    x = X0 + index * 8
    z = Z0
    execs(
        f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
        f"/setblock {x} {Y} {z} industrialreforged:{machine}",
        f"/kill @e[type=item,x={x},y={Y},z={z},distance=..8]",
    )

    # put something inside first (best effort: try the common container names)
    for container in ("input", "fuel", "charge", "contents", "waterInput"):
        execs(f'/data merge block {x} {Y} {z} {{{container}:[{{Slot:0b,id:"{content}",count:1}}]}}')

    # fake player holds the tool and breaks the block
    call("/items", {"player": "fake", "slot": 0, "item": tool, "count": 1})
    call("/break", {"x": x, "y": Y, "z": z, "player": "fake"})
    call("/wait", {"ticks": 10})

    drops = entities(x, Y, z)
    expected_main = machine if expect_machine_item else casing
    full = expected_main if ":" in expected_main else "industrialreforged:" + expected_main
    ok = drops.get(full, 0) >= 1
    detail = f"{'ключ' if expect_machine_item else 'кирка'}: дроп={drops} ожидалось {full}"
    return ok, detail


def main():
    global BASE
    if "--base" in sys.argv:
        BASE = sys.argv[sys.argv.index("--base") + 1]

    results = []
    index = 0
    for machine, casing, content in CASES:
        for tool, expect_machine_item in ((PICKAXE, False), (WRENCH, True)):
            index += 1
            ok, detail = run_case(index, machine, casing, content, tool, expect_machine_item)
            results.append((machine, tool, ok, detail))
            print(f"{'PASS' if ok else 'FAIL'}  {machine:<20} {tool.split(':')[-1]:<16} {detail}")

    passed = sum(1 for _, _, ok, _ in results if ok)
    print()
    print(f"итого: {passed}/{len(results)} прошло")


if __name__ == "__main__":
    main()
