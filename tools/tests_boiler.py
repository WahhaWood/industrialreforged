#!/usr/bin/env python3
"""E2E: резервуар-котёл — вода + тепло → пар, режимы клапана, раздача пара, тепловые трубы.

Проверяет:
1. Входной клапан принимает воду в водяной бак.
2. Тепло в резервуаре кипятит воду: 1 мБ воды + 100 тепла → 100 мБ пара.
3. Ключ переключает клапан в режим выхода пара (output=true).
4. Выходной клапан отдаёт пар соседней турбине (без труб).
5. Тепловая труба передаёт тепло в резервуар.
6. Тепловая труба забирает тепло у теплогенератора.

Usage: python3 tools/tests_boiler.py
"""

import irbridge_testkit as t
from irbridge_testkit import be_field, be_set, check, execs, fluids, fluid_fill, place, wait


def build_box(min_pos, max_pos, casing="industrialreforged:tank_wall"):
    x1, y1, z1 = min_pos
    x2, y2, z2 = max_pos
    execs(f"fill {x1} {y1} {z1} {x2} {y2} {z2} {casing}")
    execs(f"fill {x1+1} {y1+1} {z1+1} {x2-1} {y2-1} {z2-1} minecraft:air")


def tank_amount(pos, index):
    tanks = fluids(pos).get("tanks", [])
    return tanks[index].get("amount", 0) if len(tanks) > index else 0


def main():
    execs("forceload add 335 -405 365 -385")
    execs("fill 335 150 -405 365 160 -385 minecraft:air")
    execs("fill 335 150 -405 365 150 -385 minecraft:iron_block")
    wait(10)

    # Коробка 3x3x3: клапан воды/режима на южной стенке, клапан тепла — на западной
    build_box((340, 151, -400), (342, 153, -398))
    valve = (341, 152, -398)
    heat_valve = (340, 152, -399)
    place(valve, "industrialreforged:tank_valve")
    place(heat_valve, "industrialreforged:tank_valve")
    wait(5)
    res = be_set(valve, action="validate")
    check("резервуар собран", res.get("validated") is True, res)
    check("ёмкость 3x3x3 = 16 000 мБ", res.get("capacity") == 16000, res.get("capacity"))

    # ---- 1. вода во входной клапан (режим по умолчанию — вход воды) ------
    fluid_fill(valve, "minecraft:water", 1000)
    wait(5)
    water = tank_amount(valve, 0)
    check("вход воды принял 1000 мБ воды", water == 1000, water)
    rejected = fluid_fill(valve, "industrialreforged:steam", 100).get("filled", 0)
    check("вход воды НЕ принимает пар", rejected == 0, rejected)

    # ---- 2. тепло кипятит воду: 1 мБ + 100 тепла -> 100 мБ пара ---------
    be_set(valve, setField="heat", value=1000)
    wait(10)
    water2 = tank_amount(valve, 0)
    steam = tank_amount(valve, 1)
    heat = int(be_field(valve, "heat"))
    check("вода ушла на кипячение (1000 -> 990)", water2 == 990, water2)
    check("получено 1000 мБ пара", steam == 1000, steam)
    check("тепло потрачено", heat <= 100, heat)

    # ---- 3. ключ переключает клапан по кругу: выход пара -----------------
    # Порядок обхода: water_in -> water_out -> steam_in -> steam_out,
    # из режима по умолчанию до выхода пара — три клика.
    use = None
    for _ in range(3):
        use = t.call("/use", {"x": valve[0], "y": valve[1], "z": valve[2],
                              "item": "industrialreforged:wrench", "player": "fake"})
    state = t.block(valve).get("state", "")
    check("ключ переключил клапан в выход пара", "mode=steam_out" in state, f"{use} / {state}")
    rejected = fluid_fill(valve, "minecraft:water", 100).get("filled", 0)
    check("выход пара НЕ принимает воду", rejected == 0, rejected)
    drained = t.call("/fluids", {"x": valve[0], "y": valve[1], "z": valve[2], "drain": True, "amount": 100})
    check("выход пара отдаёт пар", drained.get("drained") == 100 and "steam" in drained.get("fluid", ""), drained)

    # ---- 4. выходной клапан отдаёт пар соседней турбине ------------------
    be_set(valve, setField="heat", value=1000)   # ещё пар, чтобы турбина дала воду
    wait(5)
    steam_before = tank_amount(valve, 1)
    turbine = (341, 152, -397)
    place(turbine, "industrialreforged:steam_kinetic_generator")
    be_set(turbine, setField="turbineSlot", slot=0, item="industrialreforged:steel_steam_turbine", count=1)
    wait(30)
    steam_after = tank_amount(valve, 1)
    turbine_water = tank_amount(turbine, 1)
    turbine_progress = int(be_field(turbine, "progress"))
    check("выходной клапан отдал пар турбине", steam_after < steam_before, f"{steam_before} -> {steam_after}")
    check("турбина переработала пар (вода/прогресс)", turbine_water > 0 or turbine_progress > 0,
          f"вода {turbine_water}, прогресс {turbine_progress}")

    # ---- 4b. остальные режимы клапана ------------------------------------
    # Из steam_out по кругу: steam_out -> water_in -> water_out (два клика).
    for _ in range(2):
        t.call("/use", {"x": valve[0], "y": valve[1], "z": valve[2],
                        "item": "industrialreforged:wrench", "player": "fake"})
    state = t.block(valve).get("state", "")
    check("ключ довёл клапан до выхода воды", "mode=water_out" in state, state)
    drained = t.call("/fluids", {"x": valve[0], "y": valve[1], "z": valve[2], "drain": True, "amount": 100})
    check("выход воды отдаёт воду", drained.get("drained") == 100 and "water" in drained.get("fluid", ""), drained)
    t.call("/use", {"x": valve[0], "y": valve[1], "z": valve[2], "item": "industrialreforged:wrench", "player": "fake"})
    state = t.block(valve).get("state", "")
    check("ключ довёл клапан до входа пара", "mode=steam_in" in state, state)
    rejected = fluid_fill(valve, "minecraft:water", 100).get("filled", 0)
    check("вход пара НЕ принимает воду", rejected == 0, rejected)
    accepted = fluid_fill(valve, "industrialreforged:steam", 500).get("filled", 0)
    check("вход пара принимает пар", accepted == 500, accepted)

    # ---- 5. тепловая труба несёт тепло в резервуар -----------------------
    # тепло сразу тратится на кипение, поэтому проверяем испарившуюся воду
    be_set(valve, setField="heat", value=0)
    water_before = tank_amount(valve, 0)
    pipe = (339, 152, -399)
    place(pipe, "industrialreforged:heat_pipe_3")
    wait(5)  # даём сети собраться, чтобы пересборка не обнулила заданное тепло
    be_set(pipe, setField="heat", value=500)
    wait(10)
    water_after = tank_amount(valve, 0)
    pipe_heat = int(be_field(pipe, "heat"))
    check("тепловая труба передала тепло резервуару (вода испарилась)",
          water_after < water_before and pipe_heat == 0,
          f"вода {water_before}->{water_after}, труба {pipe_heat}")

    # ---- 6. тепловая труба забирает тепло у теплогенератора --------------
    gen = (338, 152, -399)
    t.setblock(gen, "industrialreforged:solid_heat_generator", "facing=east")
    be_set(gen, setField="heat", value=400)
    be_set(pipe, setField="heat", value=0)
    be_set(valve, setField="heat", value=0)
    wait(10)
    pipe_after = int(be_field(pipe, "heat"))
    gen_after = int(be_field(gen, "heat"))
    check("труба забрала тепло у теплогенератора", gen_after < 400, f"генератор {gen_after}, труба {pipe_after}")

    # ---- 7. сеть труб: общий буфер, потери растут с числом труб ----------
    p1 = (350, 155, -400)
    p2 = (351, 155, -400)
    p3 = (352, 155, -400)
    for p in (p1, p2, p3):
        place(p, "industrialreforged:heat_pipe_1")
    wait(10)
    be_set(p1, setField="heat", value=100000)   # мастер — самая младшая труба сети
    wait(10)
    net_heat = int(be_field(p1, "heat"))
    mirror = int(be_field(p2, "heat"))
    check("сеть труб держит общий буфер больше одной трубы", net_heat > 80000, net_heat)
    check("потери сети растут с числом труб", net_heat < 100000, net_heat)
    check("все трубы сети видят общее тепло", abs(mirror - net_heat) <= 2, f"{net_heat} vs {mirror}")

    return t.summary("tests_boiler")


if __name__ == "__main__":
    raise SystemExit(main())
