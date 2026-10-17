#!/usr/bin/env python3
"""E2E: гевея — латекс нельзя добыть перестановкой брёвен.

Проверяет:
1. Свежее бревно (state=plain) при рубке игроком даёт латекс + ОТРАБОТАННОЕ бревно.
2. Отработанное бревно (state=spent) не даёт латекс и дропает только отработанное.
3. Предмет «отработанное бревно» зарегистрирован (ставится и крафтится).

Usage: python3 tools/tests_rubber.py
"""

import irbridge_testkit as t
from irbridge_testkit import break_block, check, execs, place, setblock, wait


def drops_at(pos, radius=5):
    result = t.call(f"/entities?x={pos[0]}&y={pos[1]}&z={pos[2]}&r={radius}")
    return [(e.get("item"), e.get("count", 1)) for e in result.get("entities", []) if e.get("item")]


def clear_items(pos, radius=6):
    execs(f"kill @e[type=item,x={pos[0]},y={pos[1]},z={pos[2]},distance=..{radius}]")


def main():
    execs("forceload add 230 -420 320 -380")
    execs("fill 236 99 -404 244 103 -396 minecraft:air")
    execs("fill 236 99 -404 244 99 -396 minecraft:iron_block")
    wait(10)

    # ---- 1. Свежая рубка: латекс + отработанное бревно -------------------
    fresh = (240, 100, -400)
    setblock(fresh, "industrialreforged:rubber_log", "state=plain,axis=y")
    wait(5)
    clear_items(fresh)
    break_block(fresh)
    wait(10)
    items = drops_at(fresh)
    names = [name for name, _ in items]
    check("свежая рубка даёт латекс", any("resin" in name for name in names), items)
    check("свежая рубка даёт отработанное бревно", any("spent_rubber_log" in name for name in names), items)
    check("свежая рубка НЕ даёт свежее бревно", not any(name.endswith(":rubber_log") for name in names), items)

    # ---- 2. Отработанное бревно: без латекса ----------------------------
    spent = (241, 100, -400)
    setblock(spent, "industrialreforged:rubber_log", "state=spent,axis=y")
    wait(5)
    clear_items(spent)
    break_block(spent)
    wait(10)
    items2 = drops_at(spent)
    names2 = [name for name, _ in items2]
    check("отработанное бревно НЕ даёт латекс", not any("resin" in name for name in names2), items2)
    check("отработанное бревно дропает отработанное", any("spent_rubber_log" in name for name in names2), items2)

    # ---- 3. Предмет зарегистрирован (проверка без игрока) ----------------
    chest = (242, 100, -400)
    place(chest, "minecraft:chest")
    wait(3)
    output = " ".join(
        t.execs(f"item replace block {chest[0]} {chest[1]} {chest[2]} container.0 with industrialreforged:spent_rubber_log")
        .get("output", [])
    ).lower()
    check("предмет отработанного бревна зарегистрирован",
          "unknown" not in output and "неизвестн" not in output, output[:120])

    return t.summary("tests_rubber")


if __name__ == "__main__":
    raise SystemExit(main())
