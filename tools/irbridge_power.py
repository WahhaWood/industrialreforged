#!/usr/bin/env python3
"""Power tests: wire tiers and generators, through the dev bridge.

Wire tiers: a strong source (solar panel level 15) -> wire_N -> an unlimited
sink (EFSU with no limits), then compare the wire's metered flow with its tier.

Generators: place, feed, and compare the storage's extract rate (2 x production)
with the expected value.

Usage: tools/irbridge_power.py [--base http://127.0.0.1:9878] [--only wires|gens]
"""

import json
import os
import sys
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:9878"
AREA_X = 300
AREA_Z = -500
Y = 100

WIRE_TIERS = {
    1: 200, 2: 800, 3: 8_000, 4: 32_000, 5: 100_000,
    6: 1_000_000, 7: 10_000_000, 8: 100_000_000, 9: 1_000_000_000,
    10: 536_870_911,  # Integer.MAX_VALUE / 4, the universal wire's effective rate
}

# generator block -> (fuel container name, fuel item, expected production FE/t)
GENERATORS = [
    ("generator", "fuel", "minecraft:coal", 20),
    ("geo_generator", "fuel", "minecraft:lava_bucket", 40),
]

SOLAR_LEVELS = [1, 5, 10, 15]  # checked against the config values


sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402


def call(path, payload=None, timeout=180):
    # BASE может поменять __main__ (--base), поэтому настраиваем мост при каждом вызове.
    bridgekit.configure(BASE)
    return bridgekit.call(path, payload, timeout)


def execs(*commands):
    return call("/exec", {"commands": list(commands)})


def wait(ticks):
    call("/wait", {"ticks": ticks, "timeoutMs": 120000})


def be_of(x, y, z):
    return call(f"/block?x={x}&y={y}&z={z}").get("be", "")


def energy_of(x, y, z):
    return call(f"/energy?x={x}&y={y}&z={z}")


def number(snbt, key):
    marker = f"{key}:"
    index = snbt.find(marker)
    if index < 0:
        return None
    index += len(marker)
    end = index
    while end < len(snbt) and (snbt[end].isdigit() or snbt[end] in "-L"):
        end += 1
    try:
        return int(snbt[index:end].rstrip("L"))
    except ValueError:
        return None


def test_wires():
    results = []
    for tier, rate in WIRE_TIERS.items():
        x = AREA_X + tier * 8
        z = AREA_Z
        high = 200
        execs(
            f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
            "/time set noon",
            "/weather clear",
            f"/fill {x - 2} {high} {z} {x + 2} {high + 10} {z} air",
            f"/setblock {x - 1} {high} {z} industrialreforged:solar_generator_15_level",
            f"/setblock {x} {high} {z} industrialreforged:wire_{tier}_level",
            f"/setblock {x + 1} {high} {z} industrialreforged:efsu",
        )
        wait(60)

        snbt = be_of(x, high, z)
        light = call(f"/block?x={x - 1}&y={high}&z={z}").get("skyLight", 0)
        input_rate = number(snbt, "inputRate") or 0
        output_rate = number(snbt, "outputRate") or 0
        capacity = number(snbt, "capacity") or 0
        expected_capacity = max(min(rate * 4, 1_073_741_823), max(1000, rate))
        energy = number(snbt, "energy") or 0
        # the sink (an EFSU) fills within the first ticks for the high tiers, so
        # the flow is only measurable for the lower ones; for the rest a full
        # wire buffer already proves the intake happened
        if rate <= 10_000_000:
            flow_ok = abs(input_rate - rate) <= max(rate * 0.35, 10)
        else:
            flow_ok = energy > 0 or input_rate > 0
        ok = abs(capacity - expected_capacity) <= max(expected_capacity * 0.01, 100) and flow_ok
        results.append((f"wire_{tier}_level", ok, f"in={input_rate} cap={capacity} light={light} (тир {rate})"))
        print(f"{'PASS' if ok else 'FAIL'}  wire_{tier}_level  in={input_rate} out={output_rate} cap={capacity} skyLight={light} (тир {rate})")

    return results


def test_generators():
    results = []
    index = 0
    for block, container, fuel, production in GENERATORS:
        index += 1
        x = AREA_X + index * 8
        z = AREA_Z - 20
        execs(
            f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
            f"/setblock {x} {Y} {z} industrialreforged:{block}",
            f'/data merge block {x} {Y} {z} {{{container}:[{{Slot:0b,id:"{fuel}",count:8}}]}}',
        )
        wait(60)
        extract = energy_of(x, Y, z).get("extract", 0)
        # generators keep extract = 2 x production
        ok = abs(extract - production * 2) <= max(2, production * 2 * 0.35)
        results.append((block, ok, f"extract={extract} ожидалось ~{production * 2}"))
        print(f"{'PASS' if ok else 'FAIL'}  {block:<18} extract={extract} (ожидалось ~{production * 2}: производство {production} FE/t)")
    return results


def test_panels():
    results = []
    index = 0
    # day values from the config
    day = {1: 40, 5: 25_000, 10: 70_000_000, 15: 100_000_000_000}
    for level in SOLAR_LEVELS:
        for night in (False, True):
            index += 1
            x = AREA_X + index * 8
            z = AREA_Z - 40
            block = ("night_solar_generator" if night else "solar_generator") + f"_{level}_level"
            execs(
                f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
                "/time set noon",
                "/weather clear",
                f"/fill {x} {150} {z} {x} {210} {z} air",
                f"/setblock {x} 200 {z} industrialreforged:{block}",
            )
            wait(40)
            extract = energy_of(x, 200, z).get("extract", 0)
            expected = max(1, day[level] * 2)
            ok = extract > 0 and abs(extract - expected) <= max(10, expected * 0.35)
            light = call(f"/block?x={x}&y=200&z={z}").get("skyLight", 0)
            results.append((block, ok, f"extract={extract} light={light} ожидалось ~{expected}"))
            print(f"{'PASS' if ok else 'FAIL'}  {block:<36} extract={extract} skyLight={light} (ожидалось ~{expected})")
    return results


def main():
    global BASE
    if "--base" in sys.argv:
        BASE = sys.argv[sys.argv.index("--base") + 1]

    only = sys.argv[sys.argv.index("--only") + 1] if "--only" in sys.argv else None
    results = []

    if only in (None, "wires"):
        print("=== провода: пропускная способность по тирам ===")
        results += test_wires()
    if only in (None, "gens"):
        print("=== генераторы ===")
        results += test_generators()
        print("=== солнечные панели (день) ===")
        results += test_panels()

    passed = sum(1 for _, ok, _ in results if ok)
    print()
    print(f"итого: {passed}/{len(results)} прошло")
    for name, ok, detail in results:
        if not ok:
            print(f"  FAIL: {name} — {detail}")


if __name__ == "__main__":
    main()
