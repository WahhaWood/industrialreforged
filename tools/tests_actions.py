#!/usr/bin/env python3
"""Дымовой тест игровых действий: «игрок нажал — сервер выжил».

Зачем. На выделенном сервере всё, что делает игрок, должно быть безопасно: ПКМ пустой
рукой, ПКМ инструментом, ломание блока, попытка вставить предмет. Код посевов один раз
уронил тест не потому, что был сломан, а потому что инструмент передавал null — но
проверять такие пути надо системно, а не по одному.

Что делает набор: для списка блоков ставит блок на подготовленной площадке и прогоняет
набор действий (ПКМ пустой рукой, ПКМ гаечным ключом, ломание, ПКМ разными предметами).
Любая ошибка моста на этом пути означает исключение в игровом коде.

Usage: python3 tools/tests_actions.py [--base URL]
"""

import sys
import time

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, call

X, Y, Z = 180, 90, -280

# Блоки, которые чаще всего трогает игрок: машины, генераторы, накопители, мультиблок-детали.
BLOCKS = [
    "macerator_1_level", "macerator_4_level", "compressor_1_level", "extractor_1_level",
    "centrifuge", "block_cutter", "metal_former_1_level", "ore_washer_1_level",
    "blast_furnace", "iron_furnace", "induction_furnace", "molecular_transformer",
    "ore_breaker", "quantum_quarry", "nuclear_reactor", "reactor_chamber",
    "generator", "geo_generator", "rt_generator", "semifluid_generator",
    "solid_heat_generator", "electric_heat_generator", "fluid_heat_generator",
    "stirling_generator", "stirling_kinetic_generator", "wind_kinetic_generator",
    "water_kinetic_generator", "steam_kinetic_generator", "kinetic_generator",
    "manual_kinetic_generator", "electric_kinetic_generator",
    "batbox", "cesu", "mfe", "mfsu", "efsu", "efs_chargepad",
    "solar_generator_1_level", "solar_generator_5_level", "solar_connector",
    "condenser", "fermenter", "pump", "solar_distiller", "liquid_heat_exchanger",
    "brewing_barrel", "luminator", "electric_boiler", "teleporter",
    "uu_scanner", "pattern_storage", "replicator", "matter_generator",
    "molecular_farming", "crop_harvester", "cropmatron",
    "tank_valve", "tank_controller", "tank_wall", "heat_pipe_1",
    "canner", "solid_canner", "fluid_bottler", "lathe", "crop_stick",
]

# Предметы, которыми игрок тыкает в блоки.
ITEMS = [None, "industrialreforged:wrench", "minecraft:bone_meal",
         "industrialreforged:strengthening_upgrade", "industrialreforged:fuel"]


def place_block(block):
    execs(f"fill {X - 1} {Y} {Z - 1} {X + 1} {Y} {Z + 1} minecraft:air")
    execs(f"setblock {X} {Y} {Z} industrialreforged:{block}")
    wait(3)


def use(item):
    payload = {"x": X, "y": Y, "z": Z, "player": "fake"}
    if item:
        payload["item"] = item
    try:
        return call("/use", payload)
    except Exception as error:              # noqa: BLE001 - тест обязан упасть, не замереть
        return {"ERR": 0, "body": str(error)}


def break_block():
    try:
        return call("/break", {"x": X, "y": Y, "z": Z, "player": "fake"})
    except Exception as error:              # noqa: BLE001
        return {"ERR": 0, "body": str(error)}


def main():
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]
    execs("/time set noon", "/gamerule doDaylightCycle false", "/weather clear",
          f"forceload add {X - 6} {Z - 6} {X + 6} {Z + 6}",
          f"fill {X - 5} {Y - 1} {Z - 5} {X + 5} {Y - 1} {Z + 5} minecraft:stone",
          f"fill {X - 5} {Y} {Z - 5} {X + 5} {Y + 2} {Z + 5} minecraft:air")

    failures = []
    for block in BLOCKS:
        place_block(block)
        for item in ITEMS:
            result = use(item)
            if "ERR" in result:
                failures.append((block, item or "(пусто)", str(result.get("body", ""))[:160]))
                break                       # блок уже упал — дальше нет смысла
            time.sleep(0.15)

        result = break_block()
        if "ERR" in result:
            failures.append((block, "ломание", str(result.get("body", ""))[:160]))
        time.sleep(0.15)

    for block, action, detail in failures:
        print(f"  FAIL {block:26s} {action:36s} {detail}")

    check(f"действия над {len(BLOCKS)} блоками не роняют сервер", not failures,
          f"упало блоков: {len({f[0] for f in failures})}")
    return t.summary("tests_actions")


if __name__ == "__main__":
    raise SystemExit(main())
