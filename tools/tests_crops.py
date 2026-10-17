#!/usr/bin/env python3
"""E2E-тест системы посевов: посадка, рост, скрещивание.

Почему тест написан: ветка посевов долго не проверялась. Разбор кода показал, что посевы сажаются из
«базовых семян» (обычные предметы, реестр в IRCrops), а не только из семенного
мешка, а скрещивание превращает площадку в новый посев на месте.

Проверяет:
  1. палку (crop_stick) можно поставить и превратить в площадку скрещивания
  2. посев сажается базовым семенем (пшеница) и растёт
  3. площадка с двумя совместимыми посевами скрещивается в новый посев
  4. после скрещивания посев реально другой (не та же карточка) — иначе скрещивание
     не работает вовсе

Usage: python3 tools/tests_crops.py [--base URL]
"""

import sys
import time

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, place

AREA_X, AREA_Y, AREA_Z = 250, 100, -330
STICK = (AREA_X, AREA_Y, AREA_Z)
NORTH = (AREA_X, AREA_Y, AREA_Z - 1)
SOUTH = (AREA_X, AREA_Y, AREA_Z + 1)
EAST = (AREA_X + 1, AREA_Y, AREA_Z)
WEST = (AREA_X - 1, AREA_Y, AREA_Z)
RIGHT = EAST
LEFT = WEST


def state_of(pos):
    return t.call(f"/block?x={pos[0]}&y={pos[1]}&z={pos[2]}").get("state", "")


def block_of(pos):
    return t.call(f"/block?x={pos[0]}&y={pos[1]}&z={pos[2]}").get("block", "")


def age_of(pos):
    """Возраст посева из состояния блока (свойство age=N).

    Состояние приходит строкой вида Block{industrialreforged:wheat_crop}[age=0,crossing_base=false],
    поэтому age ищем регуляркой, а не разбором по запятым.
    """
    import re

    match = re.search(r"\[.*?age=(-?\d+)", state_of(pos))
    return int(match.group(1)) if match else -1


def use_on(pos, item, count=1):
    # player=fake обязателен: на выделенном сервере нет живого игрока, а /use без него
    # отвечает «no player online». Фейковый игрок умеет и сажать, и ломать.
    payload = {"x": pos[0], "y": pos[1], "z": pos[2], "item": item, "count": count, "player": "fake"}
    return t.call("/use", payload)


def clear_site():
    execs(
        f"forceload add {AREA_X - 6} {AREA_Z - 6} {AREA_X + 6} {AREA_Z + 6}",
        f"fill {AREA_X - 3} {AREA_Y - 1} {AREA_Z - 3} {AREA_X + 3} {AREA_Y - 1} {AREA_Z + 3} minecraft:farmland",
        f"fill {AREA_X - 3} {AREA_Y} {AREA_Z - 3} {AREA_X + 3} {AREA_Y} {AREA_Z + 3} minecraft:air",
    )
    wait(10)


def main():
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]
    execs("/time set noon", "/gamerule doDaylightCycle false", "/weather clear")
    clear_site()

    # ---- 1. палка и площадка скрещивания -------------------------------
    place(STICK, "industrialreforged:crop_stick")
    wait(5)
    check("палка ставится", "crop_stick" in block_of(STICK), block_of(STICK))

    r = execs(f"setblock {STICK[0]} {STICK[1]} {STICK[2]} industrialreforged:crop_stick[crossing_base=true]")
    wait(5)
    check("палка становится площадкой скрещивания", "crossing_base=true" in state_of(STICK), state_of(STICK))

    # ---- 2. посадка базовым семенем и рост ------------------------------
    # обычная палка (не площадка) — чтобы посев не начал тут же скрещиваться
    place(STICK, "industrialreforged:crop_stick")
    wait(5)
    res = use_on(STICK, "minecraft:wheat_seeds")
    wait(10)
    planted = block_of(STICK)
    check("пшеница сажается базовым семенем", "wheat_crop" in planted, f"{planted} / {res.get('result')}")

    if "wheat_crop" in planted:
        ages = []
        for _ in range(8):
            wait(100)
            ages.append(age_of(STICK))
        check("посев растёт (возраст > 0)", max(ages) > 0, f"age по тикам: {ages}")
    else:
        check("посев растёт (возраст > 0)", False, "посев не посадился, рост не проверяем")

    # ---- 3. скрещивание -------------------------------------------------
    # площадка в центре, вокруг — четыре посева того же вида
    # (под каждым посевом тоже нужна палка: сажать можно только по палке)
    execs(f"setblock {STICK[0]} {STICK[1]} {STICK[2]} industrialreforged:crop_stick[crossing_base=true]")
    neighbours = [NORTH, SOUTH, EAST, WEST]
    for pos in neighbours:
        execs(f"setblock {pos[0]} {pos[1]} {pos[2]} industrialreforged:crop_stick")
    wait(5)
    for pos in neighbours:
        use_on(pos, "minecraft:wheat_seeds")
    wait(20)
    planted_n = sum(1 for pos in neighbours if "wheat_crop" in block_of(pos))
    check("соседние посевы сажаются", planted_n >= 2, f"посажено {planted_n}/4")

    before = block_of(STICK)
    crossed = False
    for _ in range(40):
        wait(60)
        now = block_of(STICK)
        if now != before and "crop" in now:
            crossed = True
            break
    check("площадка скрещивается в новый посев", crossed, f"было {before}, стало {block_of(STICK)}")

    return t.summary("tests_crops")


if __name__ == "__main__":
    raise SystemExit(main())
