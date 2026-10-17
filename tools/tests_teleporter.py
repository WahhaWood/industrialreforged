#!/usr/bin/env python3
"""E2E-тесты телепортера: связь, авто-телепорт сущностей, межмировой режим, ПКМ игрока.

На выделенном сервере (dist=SERVER) проверки с реальным игроком пропускаются —
там нет игрока; телепорт сущностей и межмировой режим проверяются предметом.

Usage: python3 tools/tests_teleporter.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, place, skip, wait

AREA = "forceload add 430 -380 470 -350"
A = (440, 200, -365)
B = (450, 200, -365)
C = (460, 200, -365)
NETHER = (100, 100, 100)


def entities_near(pos, r=2.0, dim=None):
    query = f"/entities?x={pos[0]+0.5}&y={pos[1]+1}&z={pos[2]+0.5}&r={r}"
    if dim:
        query += f"&dim={dim}"
    return t.call(query).get("entities", [])


def has_type(entities, kind):
    return any(e.get("type") == kind for e in entities)


def link(a, b, dim=None):
    nbt = f"{{targetX:{b[0]},targetY:{b[1]},targetZ:{b[2]}" + (f',targetDim:"{dim}"' if dim else "") + "}"
    execs(f"data merge block {a[0]} {a[1]} {a[2]} {nbt}")


def main():
    execs(AREA)
    execs("fill 430 199 -380 470 199 -350 minecraft:iron_block")
    execs("fill 430 200 -380 470 200 -350 minecraft:air")
    wait(10)

    place(A, "industrialreforged:teleporter")
    place(B, "industrialreforged:teleporter")
    place(C, "industrialreforged:teleporter")
    wait(5)
    link(A, B)
    link(B, A)
    wait(10)

    # ---- 1. предмет на площадке уезжает сам (под редстоуном) ------------
    execs(f"setblock {A[0]-1} {A[1]} {A[2]} minecraft:redstone_block")
    wait(5)
    execs(f'summon minecraft:item {A[0]+0.5} {A[1]+1.5} {A[2]+0.5} {{Item:{{id:"minecraft:stone",count:1}}}}')
    wait(80)
    at_a = entities_near(A)
    at_b = entities_near(B)
    check("предмет с площадки телепортируется к цели",
          has_type(at_b, "minecraft:item") and not has_type(at_a, "minecraft:item"),
          f"A={at_a}, B={at_b}")

    # ---- 2. межмировой режим: предмет уезжает в Нижний мир --------------
    execs("execute in minecraft:the_nether run forceload add 95 95 105 105")
    execs("execute in minecraft:the_nether run fill 97 99 97 103 99 103 minecraft:blackstone")
    execs("execute in minecraft:the_nether run setblock 100 100 100 industrialreforged:teleporter")
    wait(10)
    link(A, NETHER, "minecraft:the_nether")
    wait(5)
    execs(f'summon minecraft:item {A[0]+0.5} {A[1]+1.5} {A[2]+0.5} {{Item:{{id:"minecraft:diamond",count:1}}}}')
    wait(80)
    nether_items = entities_near(NETHER, dim="minecraft:the_nether")
    check("межмировой телепорт переносит предмет в Нижний мир",
          has_type(nether_items, "minecraft:item"), nether_items)

    # ---- 3. игрок: ПКМ пустой рукой (только на клиенте) -----------------
    if t.dist() == "CLIENT":
        execs(f"tp Dev {A[0]+0.5} {A[1]+1} {A[2]-1.5}")
        wait(5)
        t.call("/use", {"x": A[0], "y": A[1], "z": A[2], "player": "Dev"})
        wait(10)
        at_b = entities_near(B)
        check("ПКМ пустой рукой телепортирует игрока к цели", has_type(at_b, "minecraft:player"), f"B={at_b}")

        execs(f"tp Dev {C[0]+0.5} {C[1]+1} {C[2]-1.5}")
        wait(5)
        t.call("/use", {"x": C[0], "y": C[1], "z": C[2], "player": "Dev"})
        wait(10)
        at_b = entities_near(B)
        at_c = entities_near(C)
        check("телепортер без цели не телепортирует",
              has_type(at_c, "minecraft:player") and not has_type(at_b, "minecraft:player"),
              f"B={at_b}, C={at_c}")
    else:
        skip("ПКМ игрока и «без цели»", "на выделенном сервере нет реального игрока")

    return t.summary("tests_teleporter")


if __name__ == "__main__":
    raise SystemExit(main())
