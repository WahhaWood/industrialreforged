#!/usr/bin/env python3
"""E2E: дорогие машины падают собой даже без гаечного ключа.

Дорогие (по фидбеку): солнечные панели, ядерный реактор, квантовый карьер, рудолом.
Остальные машины — как раньше: без ключа корпус, с ключом — собой.

Проверяет через /drops (тот же путь дропа, что и при ломании).

Usage: python3 tools/tests_drops.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, drops, execs, place, wait

AREA = "forceload add 480 -420 520 -390"
BASE = (482, 100, -405)
WRENCH = "industrialreforged:wrench"

# всегда падают собой (даже без ключа)
SELF_DROP = [
    "industrialreforged:ore_breaker",
    "industrialreforged:quantum_quarry",
    "industrialreforged:nuclear_reactor",
    "industrialreforged:solar_generator_1_level",
    "industrialreforged:solar_generator_8_level",
    "industrialreforged:solar_generator_15_level",
]

# без ключа отдают корпус (машина -> ожидаемый корпус)
CASING_DROP = [
    ("industrialreforged:macerator_1_level", "industrialreforged:machine"),
    ("industrialreforged:teleporter", "industrialreforged:advanced_machine"),
    ("industrialreforged:centrifuge", "industrialreforged:machine"),
    ("industrialreforged:block_cutter", "industrialreforged:advanced_machine"),
    ("industrialreforged:molecular_transformer", "industrialreforged:machine"),
]


def drop_ids(pos, item=None):
    body = drops(pos, item=item)
    return [d.get("item") for d in body.get("drops", [])]


def main():
    execs(AREA)
    execs("fill 480 99 -420 520 99 -390 minecraft:iron_block")
    execs("fill 480 100 -420 520 100 -390 minecraft:air")
    wait(10)

    pos_x = BASE[0]
    for block_id in SELF_DROP:
        pos = (pos_x, BASE[1], BASE[2])
        place(pos, block_id)
        wait(2)
        without = drop_ids(pos)
        with_wrench = drop_ids(pos, item=WRENCH)
        check(f"{block_id}: без ключа падает собой", block_id in without, without)
        check(f"{block_id}: с ключом падает собой", block_id in with_wrench, with_wrench)
        pos_x += 2

    for block_id, casing in CASING_DROP:
        pos = (pos_x, BASE[1], BASE[2])
        place(pos, block_id)
        wait(2)
        without = drop_ids(pos)
        with_wrench = drop_ids(pos, item=WRENCH)
        check(f"{block_id}: без ключа отдаёт корпус", casing in without and block_id not in without, without)
        check(f"{block_id}: с ключом падает собой", block_id in with_wrench, with_wrench)
        pos_x += 2

    return t.summary("tests_drops")


if __name__ == "__main__":
    raise SystemExit(main())
