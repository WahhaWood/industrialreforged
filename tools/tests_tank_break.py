#!/usr/bin/env python3
"""Регресс-тесты резервуара: поломка ЛЮБОЙ допустимой стенки распускает мультиблок,
а при сборке стенки/клапан получают active=true (своя текстура).

Проверяет:
1. Tank_wall (обычная стенка): сборка, подсветка active=true, поломка игроком -> распад.
2. Укреплённое стекло (окна): то же.
3. Укреплённый камень: больше НЕ является обшивкой — резервуар не собирается.

Usage: python3 tools/tests_tank_break.py
"""

import irbridge_testkit as t
from irbridge_testkit import block, break_block, be_field, check, execs, place, wait


def build_box(min_pos, max_pos, casing_block):
    x1, y1, z1 = min_pos
    x2, y2, z2 = max_pos
    execs(f"fill {x1} {y1} {z1} {x2} {y2} {z2} {casing_block}")
    execs(f"fill {x1+1} {y1+1} {z1+1} {x2-1} {y2-1} {z2-1} minecraft:air")


def state_at(pos):
    return block(pos).get("state", "")


def active_at(pos):
    return "active=true" in state_at(pos)


def formed_at(pos):
    return str(be_field(pos, "formed")).lower() == "true"


def scenario(name, origin_x, casing):
    """Коробка 3x3x3 из casing; клапан на южной стенке; ломаем западную стенку."""
    min_pos = (origin_x, 151, -400)
    max_pos = (origin_x + 2, 153, -398)
    build_box(min_pos, max_pos, casing)
    valve = (origin_x + 1, 152, -398)
    place(valve, "industrialreforged:tank_valve")
    wait(5)

    res = t.be_set(valve, action="validate")
    check(f"{name}: резервуар собирается", res.get("validated") is True, res)

    wall = (origin_x, 152, -399)          # западная стенка, касается полости
    other_wall = (origin_x + 2, 152, -399)  # восточная стенка
    check(f"{name}: стенка подсвечена active=true", active_at(wall), state_at(wall))
    check(f"{name}: клапан подсвечен active=true", active_at(valve), state_at(valve))

    # Ломаем стенку как игрок (реальный путь destroyBlock -> onRemove)
    break_block(wall)
    wait(5)

    check(f"{name}: клапан распущен после поломки стенки", not formed_at(valve),
          be_field(valve, "formed"))
    check(f"{name}: клапан погас (active=false)", not active_at(valve), state_at(valve))
    check(f"{name}: оставшиеся стенки погасли", not active_at(other_wall), state_at(other_wall))


def rejected(name, origin_x, casing):
    """Коробка из блока, который больше не является обшивкой: резервуар НЕ собирается."""
    min_pos = (origin_x, 151, -400)
    max_pos = (origin_x + 2, 153, -398)
    build_box(min_pos, max_pos, casing)
    valve = (origin_x + 1, 152, -398)
    place(valve, "industrialreforged:tank_valve")
    wait(5)

    res = t.be_set(valve, action="validate")
    check(f"{name}: резервуар НЕ собирается", res.get("validated") is not True, res)
    check(f"{name}: клапан не подсвечен", not active_at(valve), state_at(valve))


def main():
    execs("forceload add 335 -405 360 -390")
    execs("fill 335 150 -405 360 156 -390 minecraft:air")
    execs("fill 335 150 -405 360 150 -390 minecraft:iron_block")
    wait(10)

    scenario("стенка tank_wall", 340, "industrialreforged:tank_wall")
    scenario("укреплённое стекло", 346, "industrialreforged:reinforced_glass")
    rejected("укреплённый камень (не обшивка)", 352, "industrialreforged:reinforced_stone")

    return t.summary("tests_tank_break")


if __name__ == "__main__":
    raise SystemExit(main())
