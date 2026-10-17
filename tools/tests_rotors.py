#!/usr/bin/env python3
"""E2E-тесты роторов: износ ветряного ротора, поломка, паровая турбина (износ + пар->вода).

Usage: python3 tools/tests_rotors.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, be, be_field, be_set, fluids, fluid_fill, place

WIND = (264, 155, -400)
STEAM = (274, 151, -400)


def _first_real(fields, name):
    for entry in fields.get(name, {}).get("slots", []):
        if entry.get("item", "minecraft:air") != "minecraft:air" and entry.get("count", 0) > 0:
            return entry
    return None


def rotor_item(pos):
    return _first_real(be(pos)["fields"], "rotorSlot")


def turbine_item(pos):
    return _first_real(be(pos)["fields"], "turbineSlot")


def main():
    execs("forceload add 230 -420 320 -380")
    execs("fill 230 150 -420 320 -380 minecraft:iron_block")
    execs("fill 230 151 -420 320 -380 minecraft:air")
    execs("weather clear")
    wait(20)

    # ---- ветряк: ротор крутится и изнашивается ---------------------------
    execs(f"setblock 264 151 -400 industrialreforged:iron_block")
    execs(f"setblock 264 152 -400 industrialreforged:iron_block")
    execs(f"setblock 264 153 -400 industrialreforged:iron_block")
    execs(f"setblock 264 154 -400 industrialreforged:iron_block")
    place(WIND, "industrialreforged:wind_kinetic_generator", "facing=east")
    wait(10)
    be_set(WIND, setField="rotorSlot", slot=0, item="industrialreforged:wooden_rotor")
    wait(120)
    strength = float(be_field(WIND, "windStrength"))
    speed = float(be_field(WIND, "rotationSpeed"))
    check("на высоте 155 ветер есть и ротор крутится", strength > 0 and speed > 0, f"ветер {strength:.1f}, скорость {speed:.2f}")
    rotor = rotor_item(WIND)
    check("деревянный ротор изнашивается", rotor is not None and rotor.get("damage", 0) > 0,
          rotor if rotor is None else f"damage={rotor.get('damage')}")

    # ---- ротор ломается на нуле прочности --------------------------------
    be_set(WIND, setField="rotorSlot", slot=0, item="industrialreforged:wooden_rotor", damage=2499)
    wait(60)
    check("ротор с нулевой прочностью вылетает из слота", rotor_item(WIND) is None, rotor_item(WIND))
    speed = float(be_field(WIND, "rotationSpeed"))
    check("без ротора ветряк останавливается", speed == 0.0, speed)

    # ---- вечный иридиевый ротор не изнашивается ---------------------------
    be_set(WIND, setField="rotorSlot", slot=0, item="industrialreforged:iridium_rotor")
    wait(80)
    rotor = rotor_item(WIND)
    check("иридиевый ротор вечный", rotor is not None and "damage" not in rotor, rotor)

    # ---- паровая турбина: пар -> мощность -> вода, износ ------------------
    place(STEAM, "industrialreforged:steam_kinetic_generator")
    wait(10)
    be_set(STEAM, setField="turbineSlot", slot=0, item="industrialreforged:steam_turbine")
    fluid_fill(STEAM, "industrialreforged:steam", 20000)
    wait(30)
    steam_after = fluids(STEAM)["tanks"][0]
    water = be(STEAM)["fields"].get("waterTank", {})
    check("турбина съела пар", steam_after["amount"] == 0, steam_after)
    check("из пара конденсируется вода", water.get("amount", 0) > 0, water)
    turbine = turbine_item(STEAM)
    check("турбина изнашивается при работе", turbine is not None and turbine.get("damage", 0) > 0,
          turbine if turbine is None else f"damage={turbine.get('damage')}")

    # ---- стальная турбина принимается слотом ------------------------------
    be_set(STEAM, setField="turbineSlot", slot=0, item="industrialreforged:steel_steam_turbine")
    wait(5)
    turbine = turbine_item(STEAM)
    check("стальная турбина ставится в слот", turbine is not None and "steel_steam_turbine" in turbine.get("item", ""), turbine)

    # ---- сломанная турбина исчезает ---------------------------------------
    be_set(STEAM, setField="turbineSlot", slot=0, item="industrialreforged:steam_turbine", damage=19999)
    fluid_fill(STEAM, "industrialreforged:steam", 2000)
    wait(30)
    check("турбина с нулевой прочностью исчезает", turbine_item(STEAM) is None, turbine_item(STEAM))
    check("без турбины пар стравливается", fluids(STEAM)["tanks"][0]["amount"] == 0, fluids(STEAM)["tanks"][0])

    # ---- баланс кинетического генератора (таргет топа = 125 000 FE/t) ------
    HIGH_WIND = (264, 250, -400)
    HIGH_KINETIC = (263, 250, -400)
    execs("forceload add 260 -405 270 -395")
    execs(f"fill 255 245 -410 275 260 -390 minecraft:air")
    execs(f"setblock 264 249 -400 minecraft:iron_block")
    place(HIGH_WIND, "industrialreforged:wind_kinetic_generator", "facing=east")
    place(HIGH_KINETIC, "industrialreforged:kinetic_generator", "facing=east")

    # 1) Карбоновый ротор на пике ветра (100.0) -> 1 000 kPS -> 12 500 FE/t
    be_set(HIGH_WIND, setField="rotorSlot", slot=0, item="industrialreforged:carbon_rotor")
    wait(40)  # естественный тик включает active=true и валидирует пространство
    be_set(HIGH_WIND, setField="windStrength", value=100)
    be_set(HIGH_WIND, setField="updateTicker", value=999999)
    wait(10)
    wind_str = float(be_field(HIGH_WIND, "windStrength"))
    k_prod = int(be_field(HIGH_KINETIC, "production"))
    check("на пике ветра сила ветра равна 100", wind_str >= 99.0, f"ветер {wind_str}")
    check("карбоновый ротор дает ~12 500 FE/t", k_prod == 12500, f"production={k_prod} FE/t")

    # 2) Топовый вольфрамовый ротор -> 10 000 kPS -> ровно 125 000 FE/t (уровень панели 6)
    be_set(HIGH_WIND, setField="rotorSlot", slot=0, item="industrialreforged:tungsten_rotor")
    wait(40)  # естественный тик обновляет параметры ротора и подтверждает active=true
    be_set(HIGH_WIND, setField="windStrength", value=100)
    be_set(HIGH_WIND, setField="updateTicker", value=999999)
    wait(10)
    k_prod_top = int(be_field(HIGH_KINETIC, "production"))
    check("вольфрамовый ротор достигает таргета топа: 125 000 FE/t (панель 6 ур.)",
          k_prod_top == 125000, f"production={k_prod_top} FE/t")

    return t.summary("tests_rotors")


if __name__ == "__main__":
    raise SystemExit(main())
