#!/usr/bin/env python3
"""Extra mechanics: storages charging batteries, solar distiller group, pump,
panel connector.

Usage: tools/irbridge_extra.py [--base http://127.0.0.1:9878]
"""

import json
import re
import os
import sys
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:9878"
X, Y, Z = 600, 100, -800

STORAGES = [
    ("batbox", 250_000),
    ("cesu", 1_000_000),
    ("mfe", 4_000_000),
    ("mfsu", 16_000_000),
    ("efsu", 1_000_000_000),
]


sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402


def call(path, payload=None, timeout=180):
    # BASE может поменять __main__ (--base), поэтому настраиваем мост при каждом вызове.
    bridgekit.configure(BASE)
    return bridgekit.call(path, payload, timeout)


def execs(*commands):
    return call("/exec", {"commands": list(commands)})


def wait(ticks):
    call("/wait", {"ticks": ticks, "timeoutMs": 180000})


def be(x, y, z):
    return call(f"/block?x={x}&y={y}&z={z}").get("be", "")


def battery_charge(snbt):
    match = re.search(r'"industrialreforged:energy":(\d+)', snbt)
    return int(match.group(1)) if match else 0


def money(snbt, key):
    match = re.search(key + r":(\d+)", snbt)
    return int(match.group(1)) if match else 0


def test_storages():
    results = []
    for index, (block, capacity) in enumerate(STORAGES):
        x = X + index * 8
        z = Z
        execs(
            f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
            f"/setblock {x} {Y} {z} industrialreforged:{block}",
            f'/data merge block {x} {Y} {z} {{energy:{capacity // 2},charge:[{{Slot:0b,id:"industrialreforged:re_battery",count:1}}]}}',
        )
        wait(60)
        snbt = be(x, Y, z)
        charge = battery_charge(snbt)
        stored = money(snbt, "energy")
        expected_battery = min(10_000, capacity // 2)
        ok = charge > 0 and abs((capacity // 2 - stored) - charge) <= 2
        results.append((block, ok, f"батарейка={charge}, блок {capacity // 2} -> {stored} (перенос {capacity // 2 - stored})"))
        print(f"{'PASS' if ok else 'FAIL'}  {block:<8} батарейка={charge} (ждали до {expected_battery}), энергия блока {stored}")
    return results


def test_distiller():
    results = []
    x, z = X, Z - 20
    execs(
        f"/forceload add {x - 8} {z - 8} {x + 16} {z + 8}",
        "/time set noon",
        "/gamerule doDaylightCycle false",
        f"/setblock {x} {Y + 6} {z} industrialreforged:solar_distiller",
        f'/data merge block {x} {Y + 6} {z} {{waterInput:[{{Slot:0b,id:"minecraft:water_bucket",count:1}}]}}',
    )
    wait(80)
    tanks = call(f"/fluids?x={x}&y={Y + 6}&z={z}").get("tanks", [])
    distilled = next((t for t in tanks if "distilled" in str(t.get("fluid"))), None)
    single_ok = distilled is not None and distilled.get("amount", 0) > 0
    results.append(("дистиллятор: вода -> дистиллят", single_ok, str(tanks)))
    print(f"{'PASS' if single_ok else 'FAIL'}  дистиллятор одиночный: {tanks}")

    # second distiller joins the group -> shared buffers double the capacity
    execs(
        f"/setblock {x + 1} {Y + 6} {z} industrialreforged:solar_distiller",
        f'/data merge block {x + 1} {Y + 6} {z} {{waterInput:[{{Slot:0b,id:"minecraft:water_bucket",count:1}}]}}',
    )
    wait(80)
    tanks_a = call(f"/fluids?x={x}&y={Y + 6}&z={z}").get("tanks", [])
    tanks_b = call(f"/fluids?x={x + 1}&y={Y + 6}&z={z}").get("tanks", [])
    cap_a = max((t.get("capacity", 0) for t in tanks_a), default=0)
    cap_b = max((t.get("capacity", 0) for t in tanks_b), default=0)
    same = tanks_a == tanks_b
    group_ok = cap_a >= 20_000 and cap_b >= 20_000 and same
    results.append(("дистиллятор: общий буфер на двоих", group_ok, f"capacity {cap_a}/{cap_b}, одинаковые={same}"))
    print(f"{'PASS' if group_ok else 'FAIL'}  группа дистилляторов: ёмкость {cap_a} и {cap_b}, буферы совпадают={same}")
    return results


def test_pump():
    x, z = X, Z - 40
    execs(
        f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
        f"/fill {x - 1} {Y - 3} {z - 1} {x + 1} {Y - 1} {z + 1} minecraft:water",
        f"/setblock {x} {Y} {z} industrialreforged:pump",
        f"/data merge block {x} {Y} {z} {{energy:2048}}",
    )
    wait(60)
    tanks = call(f"/fluids?x={x}&y={Y}&z={z}").get("tanks", [])
    total = sum(t.get("amount", 0) for t in tanks)
    ok = total > 0
    print(f"{'PASS' if ok else 'FAIL'}  помпа: {tanks}")
    return [("помпа качает воду", ok, str(tanks))]


def test_connector():
    results = []
    x, z = X, Z - 60
    execs(
        f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
        "/time set noon",
        f"/setblock {x} {Y + 6} {z} minecraft:air",
        f"/setblock {x} {Y + 6} {z} industrialreforged:solar_connector",
    )
    wait(40)
    empty = call(f"/energy?x={x}&y={Y + 6}&z={z}")
    empty_ok = empty.get("extract", 0) == 0
    results.append(("коннектор без панелей = 0 FE/t", empty_ok, str(empty.get("extract"))))
    print(f"{'PASS' if empty_ok else 'FAIL'}  коннектор без панелей: extract={empty.get('extract')}")

    execs(f'/data merge block {x} {Y + 6} {z} {{inventory:[{{Slot:0b,id:"industrialreforged:solar_generator_1_level",count:1}}]}}')
    wait(40)
    with_panel = call(f"/energy?x={x}&y={Y + 6}&z={z}")
    panel_ok = with_panel.get("extract", 0) > 0
    results.append(("коннектор с панелью производит", panel_ok, f"extract={with_panel.get('extract')}"))
    print(f"{'PASS' if panel_ok else 'FAIL'}  коннектор с панелью 1 ур.: extract={with_panel.get('extract')} (ждём 2x40=80)")
    return results


def main():
    global BASE
    if "--base" in sys.argv:
        BASE = sys.argv[sys.argv.index("--base") + 1]

    print("=== хранилища: зарядка батареек ===")
    results = test_storages()
    print("=== солнечный дистиллятор (один и группа) ===")
    results += test_distiller()
    print("=== помпа ===")
    results += test_pump()
    print("=== соединитель панелей ===")
    results += test_connector()

    passed = sum(1 for _, ok, _ in results if ok)
    print()
    print(f"итого: {passed}/{len(results)} прошло")
    for name, ok, detail in results:
        if not ok:
            print(f"  FAIL: {name} — {detail}")


if __name__ == "__main__":
    main()
