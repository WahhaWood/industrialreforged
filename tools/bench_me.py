#!/usr/bin/env python3
"""Замер нагрузки ME-машин: цепочка автокрафта через крафтовый процессор AE2.

руда (c:ores/iron) -> ME-дробитель -> дроблёное железо (x2) -> ME-печь -> слиток
-> ME-формовщик (прокатка) -> пластина -> ME-компрессор (9 шт.) -> плотная пластина.

Печатает время тика сервера (/perf) в простое и во время крафта, длительность крафта в тиках
и сверяет счёт предметов: заказ выполнен, руды ушло ровно сколько нужно, промежуточных не осталось.

Usage: IRBRIDGE_PORT=9879 python3 tools/bench_me.py [плотных_пластин=1500] [ae2|quantum]
quantum — квантовый компьютер AdvancedAE вместо CPU AE2 (мод должен быть загружен).
"""

import json
import sys

import irbridge_testkit as t
from irbridge_testkit import call, check, execs, wait

Y = 200
AREA = "forceload add 700 -600 730 -560"
C1, C2, C3 = (710, Y, -580), (711, Y, -580), (712, Y, -580)  # линия контроллеров
CELL = (709, Y, -580)
DRIVE = (713, Y, -580)
CPU = (710, Y + 1, -580)  # куб 2x2x2 из 256k
MAC = (710, Y, -579)
FURNACE = (710, Y, -581)
FORMER = (711, Y, -579)
COMPRESSOR = (711, Y, -581)

ORE = "minecraft:iron_ore"
CRUSHED = "industrialreforged:crushed_iron"
INGOT = "minecraft:iron_ingot"
PLATE = "industrialreforged:iron_plate"
DENSE = "industrialreforged:dense_iron_plate"


def ae2(op, pos, **params):
    payload = {"op": op, "x": pos[0], "y": pos[1], "z": pos[2]}
    payload.update(params)
    return call("/ae2", payload)


def stored(item):
    return int(ae2("count", C1, item=item).get("count", -1))


def perf(reset=False):
    return call("/perf", {"reset": reset})


def game_time():
    return int(call("/status", {}).get("gameTime", 0))


def build(cpu_kind):
    execs(AREA)
    execs(f"fill 705 {Y - 2} -585 716 {Y + 4} -575 minecraft:air")
    wait(5)
    execs(*(f"setblock {p[0]} {p[1]} {p[2]} ae2:controller" for p in (C1, C2, C3)))
    execs(f"setblock {CELL[0]} {CELL[1]} {CELL[2]} ae2:creative_energy_cell",
          f"setblock {DRIVE[0]} {DRIVE[1]} {DRIVE[2]} ae2:drive",
          f"setblock {MAC[0]} {MAC[1]} {MAC[2]} industrialreforged:me_macerator",
          f"setblock {FURNACE[0]} {FURNACE[1]} {FURNACE[2]} industrialreforged:me_furnace",
          f"setblock {FORMER[0]} {FORMER[1]} {FORMER[2]} industrialreforged:me_metal_former",
          f"setblock {COMPRESSOR[0]} {COMPRESSOR[1]} {COMPRESSOR[2]} industrialreforged:me_compressor")
    if cpu_kind == "quantum":
        # оболочка 3x3x3 из рамки, ядро в центре; стоит на линии контроллеров
        execs(f"fill 710 {Y + 1} -582 712 {Y + 3} -580 advanced_ae:quantum_structure hollow",
              f"setblock 711 {Y + 2} -581 advanced_ae:quantum_core")
    else:
        execs(f"fill {CPU[0]} {CPU[1]} {CPU[2]} {CPU[0] + 1} {CPU[1] + 1} {CPU[2] + 1} ae2:256k_crafting_storage")
    wait(20)
    for _ in range(2):
        ae2("put", DRIVE, item="ae2:item_storage_cell_256k")
    # печь: только дроблёное железо -> слиток, иначе AE2 может плавить руду напрямую
    ae2("toggle", FURNACE, action="DISABLE_ALL")
    ae2("toggle", FURNACE, action="TOGGLE", recipe="industrialreforged:smelting/crushed_iron")
    wait(40)


def main():
    dense = int(sys.argv[1]) if len(sys.argv) > 1 else 1500
    cpu_kind = sys.argv[2] if len(sys.argv) > 2 else "ae2"
    plates = dense * 9
    ore_needed = (plates + 1) // 2

    build(cpu_kind)
    info = ae2("info", C1)
    check("сеть: CPU собран, питание есть", info.get("cpus") == 1 and info.get("powered") is True, str(info))
    ae2("insert", C1, item=ORE, count=ore_needed + 1000)
    wait(10)

    before = {item: stored(item) for item in (ORE, CRUSHED, INGOT, PLATE, DENSE)}
    perf(reset=True)
    wait(100)
    idle = perf(reset=True)

    craft = ae2("craft", C1, item=DENSE, count=dense)
    check(f"заказ {dense} плотных пластин принят", craft.get("ok") is True, str(craft))
    start = game_time()
    perf(reset=True)
    done_at = None
    for _ in range(2400):
        wait(5)
        if ae2("info", C1).get("cpusBusy", 1) == 0:
            done_at = game_time()
            break
    busy = perf(reset=True)
    after = {item: stored(item) for item in (ORE, CRUSHED, INGOT, PLATE, DENSE)}

    check("крафт завершён", done_at is not None, "CPU всё ещё занят")
    check(f"в сети +{dense} плотных пластин", after[DENSE] - before[DENSE] == dense, f"{before[DENSE]} -> {after[DENSE]}")
    check(f"руды ушло {ore_needed}", before[ORE] - after[ORE] == ore_needed, f"{before[ORE]} -> {after[ORE]}")
    leftover = {item: after[item] - before[item] for item in (CRUSHED, INGOT, PLATE)}
    check("промежуточные не потерялись и не размножились (остаток от нечётной руды не больше 1)",
          all(0 <= v <= 1 for v in leftover.values()), str(leftover))

    result = {
        "dense": dense,
        "crafts": ore_needed + plates * 2 + dense,
        "craftTicks": (done_at - start) if done_at else None,
        "idle": idle,
        "crafting": busy,
    }
    print(json.dumps(result, ensure_ascii=False, indent=1))
    # площадку не оставляем: без мода блоки квантового компьютера в мире стали бы неизвестными
    execs(f"fill 705 {Y - 2} -585 716 {Y + 4} -575 minecraft:air", AREA.replace(" add ", " remove "))
    return t.summary("bench_me")


if __name__ == "__main__":
    raise SystemExit(main())
