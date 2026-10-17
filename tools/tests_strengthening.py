#!/usr/bin/env python3
"""E2E-тест улучшения «Укрепление»: одна операция обрабатывает сразу 4 предмета
(×4 за каждое улучшение) в каждом входном слоте.

Usage: python3 tools/tests_strengthening.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, be_set, energy_set, place, clear_container, fluid_fill

AREA = "forceload add 230 -380 280 -350"
MAC = (250, 200, -365)
REP = (270, 200, -365)


def field_slots(pos, field):
    entry = t.be(pos).get("fields", {}).get(field, {})
    return entry.get("slots", []) if isinstance(entry, dict) else []


def count_of(pos, field, item):
    return sum(int(s.get("count", 0)) for s in field_slots(pos, field) if s.get("item") == item)


def fresh():
    place(MAC, "industrialreforged:macerator_4_level")
    wait(5)
    clear_container(MAC, "inputSlot", 4)
    clear_container(MAC, "outputSlot", 4)
    clear_container(MAC, "upgradeSlot", 8)
    energy_set(MAC, 10_000_000)
    wait(5)


def main():
    execs(AREA)
    execs("fill 230 199 -380 280 199 -350 minecraft:iron_block")
    execs("fill 230 200 -380 280 200 -350 minecraft:air")
    wait(10)

    # ---- 1. без укрепления: одна операция = 1 предмет --------------------
    fresh()
    be_set(MAC, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=64)
    wait(360)
    left = count_of(MAC, "inputSlot", "minecraft:iron_ore")
    out = count_of(MAC, "outputSlot", "industrialreforged:crushed_iron")
    check("без укрепления: за операцию тратится 1 руда", left == 63, f"осталось {left}")
    check("без укрепления: выходит 2 пыли", out == 2, f"пыли {out}")

    # ---- 2. укрепление ×1: 4 предмета за операцию, в каждом слоте --------
    fresh()
    be_set(MAC, setField="upgradeSlot", slot=0, item="industrialreforged:strengthening_upgrade", count=1)
    be_set(MAC, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=64)
    be_set(MAC, setField="inputSlot", slot=1, item="minecraft:gold_ore", count=64)
    wait(620)
    iron_left = count_of(MAC, "inputSlot", "minecraft:iron_ore")
    gold_left = count_of(MAC, "inputSlot", "minecraft:gold_ore")
    iron_out = count_of(MAC, "outputSlot", "industrialreforged:crushed_iron")
    gold_out = count_of(MAC, "outputSlot", "industrialreforged:crushed_gold")
    check("укрепление ×1: за операцию уходит 4 руды", iron_left == 60, f"железа {iron_left}")
    check("укрепление ×1: во втором слоте тоже 4", gold_left == 60, f"золота {gold_left}")
    check("укрепление ×1: выходит 8 пыли", iron_out == 8, f"железной пыли {iron_out}")
    check("укрепление ×1: золотой пыли тоже 8", gold_out == 8, f"золотой пыли {gold_out}")

    # ---- 3. только одно укрепление на механизм (кап) --------------------
    fresh()
    be_set(MAC, setField="upgradeSlot", slot=0, item="industrialreforged:strengthening_upgrade", count=1)
    be_set(MAC, setField="upgradeSlot", slot=1, item="industrialreforged:strengthening_upgrade", count=1)
    be_set(MAC, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=64)
    wait(10)
    wait(620)
    left = count_of(MAC, "inputSlot", "minecraft:iron_ore")
    out = count_of(MAC, "outputSlot", "industrialreforged:crushed_iron")
    check("второе укрепление не усиливает (только 1 на механизм)", left == 60 and out == 8,
          f"осталось {left}, пыли {out}")

    # ---- 4. рудолом: укрепление ломает 4 руды за операцию в каждой дорожке ----
    OB = (260, 200, -365)
    place(OB, "industrialreforged:ore_breaker")
    wait(5)
    clear_container(OB, "inputSlot", 9)
    clear_container(OB, "outputSlot", 9)
    clear_container(OB, "upgradeSlot", 4)
    be_set(OB, setField="upgradeSlot", slot=0, item="industrialreforged:strengthening_upgrade", count=1)
    for lane in range(9):
        be_set(OB, setField="inputSlot", slot=lane, item="minecraft:iron_ore", count=4)
    wait(10)
    # энергия включается после раскладки — все дорожки стартуют одновременно
    energy_set(OB, 10_000_000)
    wait(150)
    left = count_of(OB, "inputSlot", "minecraft:iron_ore")
    out = count_of(OB, "outputSlot", "minecraft:raw_iron")
    check("рудолом: укрепление ломает 4 руды за операцию в каждой дорожке", left == 0 and out == 36,
          f"осталось руды {left}, дропа {out}")

    # ---- 5. репликатор: укрепление выдаёт 4 предмета за операцию ---------
    place(REP, "industrialreforged:replicator")
    wait(5)
    energy_set(REP, 2_000_000)
    fluid_fill(REP, "industrialreforged:uu_matter", 16000)
    be_set(REP, setField="refreshCooldown", value=100000)
    be_set(REP, setField="pattern", item="minecraft:iron_ingot", count=1)
    be_set(REP, setField="patternUu", value=1000)
    be_set(REP, setField="upgradeSlot", slot=0, item="industrialreforged:strengthening_upgrade", count=1)
    execs(f"data merge block {REP[0]} {REP[1]} {REP[2]} {{mode:2}}")
    wait(10)
    wait(450)
    out = count_of(REP, "outputSlot", "minecraft:iron_ingot")
    check("репликатор: укрепление выдаёт 4 предмета за операцию", out == 4, f"вышло {out}")

    return t.summary("tests_strengthening")


if __name__ == "__main__":
    raise SystemExit(main())
