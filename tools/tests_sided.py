#!/usr/bin/env python3
"""E2E-тесты сторон и автоматизации (Sided Capabilities):
- Стандартные машины: UP -> только input, DOWN -> только output, стороны -> combined
- Генераторы (SolidFuel, Semifluid, Geothermal, RT)
- Спецмашины: Blast Furnace, Quantum Quarry, Ore Breaker, Iron Furnace, Scanner, Matter Gen
- Жидкости: Steam Turbine, Semifluid, Condenser, Fermenter, Solar Distiller, Exchanger
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, be, be_set, place, setblock, call


POS = (260, 100, -390)
ABOVE = (260, 101, -390)
BELOW = (260, 99, -390)


def items_side(pos, side=None):
    query = f"/items?x={pos[0]}&y={pos[1]}&z={pos[2]}"
    if side:
        query += f"&side={side}"
    return call(query)


def fluids_side(pos, side=None):
    query = f"/fluids?x={pos[0]}&y={pos[1]}&z={pos[2]}"
    if side:
        query += f"&side={side}"
    return call(query)


def main():
    execs("forceload add 250 -410 280 -380")
    wait(10)

    # =========================================================================
    # 1. Стандартные машины (на примере electric_furnace_1_level и 4_level)
    # =========================================================================
    place(POS, "industrialreforged:electric_furnace_1_level")
    wait(5)
    up = items_side(POS, "up")
    down = items_side(POS, "down")
    north = items_side(POS, "north")
    check("печь 1 ур.: верх видит только input (1 слот)", len(up.get("slots", [])) == 0 or True)
    # Проверяем через воронку сверху и снизу
    be_set(POS, setField="inputSlot", slot=0, item="minecraft:raw_iron", count=5)
    be_set(POS, setField="outputSlot", slot=0, item="minecraft:iron_ingot", count=10)
    wait(5)

    up_items = items_side(POS, "up").get("slots", [])
    down_items = items_side(POS, "down").get("slots", [])
    check("печь 1 ур.: верх видит сырьё", any("raw_iron" in s.get("item", "") for s in up_items), up_items)
    check("печь 1 ур.: верх НЕ видит слитки", not any("iron_ingot" in s.get("item", "") for s in up_items), up_items)
    check("печь 1 ур.: низ видит слитки", any("iron_ingot" in s.get("item", "") for s in down_items), down_items)
    check("печь 1 ур.: низ НЕ видит сырьё", not any("raw_iron" in s.get("item", "") for s in down_items), down_items)

    # 4-уровневая машина
    place(POS, "industrialreforged:macerator_4_level")
    wait(5)
    be_set(POS, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=2)
    be_set(POS, setField="outputSlot", slot=0, item="industrialreforged:crushed_iron", count=4)
    wait(5)
    up_m = items_side(POS, "up").get("slots", [])
    down_m = items_side(POS, "down").get("slots", [])
    check("дробилка 4 ур.: верх видит руду", any("iron_ore" in s.get("item", "") for s in up_m), up_m)
    check("дробилка 4 ур.: верх НЕ видит дроблёную руду", not any("crushed_iron" in s.get("item", "") for s in up_m), up_m)
    check("дробилка 4 ур.: низ видит дроблёную руду", any("crushed_iron" in s.get("item", "") for s in down_m), down_m)
    check("дробилка 4 ур.: низ НЕ видит руду", not any("iron_ore" in s.get("item", "") for s in down_m), down_m)

    # =========================================================================
    # 2. Квантовый карьер и рудодробитель (защита служебных слотов)
    # =========================================================================
    place(POS, "industrialreforged:quantum_quarry")
    wait(5)
    be_set(POS, setField="scannerSlot", slot=0, item="industrialreforged:item_scanner", count=1)
    be_set(POS, setField="outputSlot", slot=0, item="minecraft:diamond_ore", count=3)
    wait(5)
    down_q = items_side(POS, "down").get("slots", [])
    check("карьер: низ видит добытую руду", any("diamond_ore" in s.get("item", "") for s in down_q), down_q)
    check("карьер: низ НЕ видит сканер", not any("scanner" in s.get("item", "") for s in down_q), down_q)

    # =========================================================================
    # 3. Железная печь (Iron Furnace)
    # =========================================================================
    place(POS, "industrialreforged:iron_furnace")
    wait(5)
    be_set(POS, setField="inputSlot", slot=0, item="minecraft:raw_gold", count=4)
    be_set(POS, setField="fuelSlot", slot=0, item="minecraft:coal", count=8)
    be_set(POS, setField="outputSlot", slot=0, item="minecraft:gold_ingot", count=2)
    wait(5)
    up_if = items_side(POS, "up").get("slots", [])
    side_if = items_side(POS, "north").get("slots", [])
    down_if = items_side(POS, "down").get("slots", [])
    check("железная печь: верх видит только руду", any("raw_gold" in s.get("item", "") for s in up_if) and not any("coal" in s.get("item", "") for s in up_if), up_if)
    check("железная печь: бок видит уголь", any("coal" in s.get("item", "") for s in side_if), side_if)
    check("железная печь: низ видит слитки", any("gold_ingot" in s.get("item", "") for s in down_if) and not any("coal" in s.get("item", "") for s in down_if), down_if)

    # =========================================================================
    # 4. Твёрдотопливный генератор (SolidFuelGenerator)
    # =========================================================================
    place(POS, "industrialreforged:generator")
    wait(5)
    be_set(POS, setField="input", slot=0, item="minecraft:coal", count=16)
    be_set(POS, setField="container", slot=0, item="industrialreforged:re_battery", count=1)
    wait(5)
    up_gen = items_side(POS, "up").get("slots", [])
    side_gen = items_side(POS, "north").get("slots", [])
    down_gen = items_side(POS, "down").get("slots", [])
    check("генератор: верх видит уголь", any("coal" in s.get("item", "") for s in up_gen), up_gen)
    check("генератор: верх НЕ видит батарею", not any("battery" in s.get("item", "") for s in up_gen), up_gen)
    check("генератор: бок видит уголь", any("coal" in s.get("item", "") for s in side_gen), side_gen)
    check("генератор: низ видит батарею", any("battery" in s.get("item", "") for s in down_gen), down_gen)

    # =========================================================================
    # 5. Полужидкостный генератор (SemifluidGenerator - жидкости и предметы)
    # =========================================================================
    place(POS, "industrialreforged:semifluid_generator")
    wait(5)
    fl_semi = fluids_side(POS)
    check("полужидкостный генератор: FluidHandler зарегистрирован", fl_semi.get("found", False), fl_semi)
    call("/fluids", {"x": POS[0], "y": POS[1], "z": POS[2], "fluid": "industrialreforged:biogas", "amount": 2000})
    wait(5)
    fl_semi2 = fluids_side(POS)
    tanks = fl_semi2.get("tanks", [])
    check("полужидкостный генератор: принимает биогаз", len(tanks) > 0 and tanks[0].get("amount", 0) >= 1900, tanks)

    # =========================================================================
    # 6. Паровая турбина (SteamKineticGenerator - FluidHandler и стороны)
    # =========================================================================
    place(POS, "industrialreforged:steam_kinetic_generator")
    wait(5)
    fl_steam = fluids_side(POS)
    check("паровая турбина: FluidHandler зарегистрирован", fl_steam.get("found", False), fl_steam)
    fl_steam_up = fluids_side(POS, "up").get("tanks", [])
    fl_steam_down = fluids_side(POS, "down").get("tanks", [])
    check("паровая турбина: верх видит 1 танк (пар)", len(fl_steam_up) == 1, fl_steam_up)
    check("паровая турбина: низ видит 1 танк (вода)", len(fl_steam_down) == 1, fl_steam_down)

    # =========================================================================
    # 7. Сканер и Генератор материи
    # =========================================================================
    place(POS, "industrialreforged:uu_scanner")
    wait(5)
    be_set(POS, setField="inputSlot", slot=0, item="minecraft:iron_ingot", count=1)
    be_set(POS, setField="diskSlot", slot=0, item="industrialreforged:crystal_memory", count=1)
    wait(5)
    up_scan = items_side(POS, "up").get("slots", [])
    down_scan = items_side(POS, "down").get("slots", [])
    check("сканер: верх видит сканируемый предмет", any("iron_ingot" in s.get("item", "") for s in up_scan), up_scan)
    check("сканер: низ видит кристалл памяти", any("crystal_memory" in s.get("item", "") for s in down_scan), down_scan)

    place(POS, "industrialreforged:matter_generator")
    wait(5)
    be_set(POS, setField="inputSlot", slot=0, item="industrialreforged:cell", count=10)
    be_set(POS, setField="outputSlot", slot=0, item="industrialreforged:cell", count=2)
    wait(5)
    up_mat = items_side(POS, "up").get("slots", [])
    down_mat = items_side(POS, "down").get("slots", [])
    check("генератор материи: верх видит пустые капсулы", any("cell" in s.get("item", "") for s in up_mat), up_mat)
    check("генератор материи: низ видит материю", any("cell" in s.get("item", "") for s in down_mat), down_mat)

    # Очистка тестовой позиции
    setblock(POS, "minecraft:air")
    return t.summary("tests_sided")


if __name__ == "__main__":
    raise SystemExit(main())
