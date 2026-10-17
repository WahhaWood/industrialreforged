#!/usr/bin/env python3
"""Перекрёстная проверка жидкостей: каждая жидкость в каждый бак.

Зачем. Работа с жидкостями — самая частая причина падений в модах: бак без фильтра
принимает что угодно, потом код машины сравнивает тип, находит неожиданный и падает.
Здесь каждая зарегистрированная жидкость заливается в бак каждой машины с жидкостью,
плюс проверяется крайний случай — заливка больше ёмкости бака.

Usage: python3 tools/tests_fluids.py [--base URL]
"""

import sys

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, call

X, Y, Z = 170, 90, -260

FLUIDS = [
    "minecraft:water", "minecraft:lava",
    "industrialreforged:steam", "industrialreforged:coolant", "industrialreforged:hot_coolant",
    "industrialreforged:biomass", "industrialreforged:biogas", "industrialreforged:distilled_water",
    "industrialreforged:hot_water", "industrialreforged:pahoehoe_lava",
    "industrialreforged:uu_matter", "industrialreforged:construction_foam",
]

# Блоки с баками (ёмкость заведомо больше 1000 мБ, чтобы заливка точно влезла).
TANKS = [
    "solar_distiller", "condenser", "fermenter", "pump", "liquid_heat_exchanger",
    "brewing_barrel", "fluid_bottler", "canner", "tank_valve",
]

# Заведомо огромные объёмы: код机器 не должен падать и на них.
HUGE = [Integer_MAX := 2_147_483_647, 1_000_000, 0]


def main():
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]
    execs("/time set noon", f"forceload add {X - 6} {Z - 6} {X + 6} {Z + 6}",
          f"fill {X - 5} {Y - 1} {Z - 5} {X + 5} {Y - 1} {Z + 5} minecraft:stone",
          f"fill {X - 5} {Y} {Z - 5} {X + 5} {Y + 2} {Z + 5} minecraft:air")

    failures = []
    filled_somewhere = False

    for block in TANKS:
        execs(f"setblock {X} {Y} {Z} industrialreforged:{block}")
        wait(3)
        for fluid in FLUIDS:
            for amount in (1000, HUGE[0]):
                try:
                    result = call("/fluids", {"x": X, "y": Y, "z": Z,
                                              "fluid": fluid, "amount": amount})
                except Exception as error:      # noqa: BLE001
                    failures.append((block, fluid, f"{amount}: {error}"[:150]))
                    continue
                if result.get("filled", 0) > 0:
                    filled_somewhere = True
                wait(2)                          # дать баку отработать тик

    for block, fluid, detail in failures:
        print(f"  FAIL {block:24s} {fluid:38s} {detail}")

    check("залив всех жидкостей во все баки не роняет сервер", not failures,
          f"комбинаций: {len(TANKS) * len(FLUIDS) * 2}, ошибок: {len(failures)}")
    check("хотя бы один бак реально принял жидкость (проверка не вхолостую)",
          filled_somewhere, "ни один залив не дал filled > 0")

    return t.summary("tests_fluids")


if __name__ == "__main__":
    raise SystemExit(main())
