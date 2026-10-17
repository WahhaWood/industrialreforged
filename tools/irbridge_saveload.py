#!/usr/bin/env python3
"""Persistence test: set up a rig, restart the server, compare the state.

Проверяет, что после перезапуска сервера выживают: топливо генератора, вход
машины, батарейка в МФЭХ, вода в дистилляторе, цель/измерение телепортера,
стержень и тепло реактора, укрепление в дробилке и вода в мультиблоке-резервуаре.

Usage:
  tools/irbridge_saveload.py --phase setup --base http://127.0.0.1:9878
  ...restart the dev server...
  tools/irbridge_saveload.py --phase check --base http://127.0.0.1:9878
"""

import json
import os
import re
import sys
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:9878"
X, Y, Z = 500, 100, -700
DUMP = "/tmp/opencode/saveload.json"

# координаты стенда
TELEPORTER = (X + 6, Y, Z)
REACTOR = (X + 7, Y, Z)
MACERATOR = (X + 13, Y, Z)
TANK_VALVE = (X + 11, Y, Z - 1)
TANK_CONTROLLER = (X + 11, Y, Z + 1)
# блоки с топливом и буферами: проверка ключей NBT (сессия 35)
IRON_FURNACE = (X + 14, Y, Z)
SOLID_HEAT = (X + 15, Y, Z)
STIRLING_KU = (X + 16, Y, Z)
WALL = (X + 17, Y, Z)


sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402


def call(path, payload=None, timeout=120):
    # BASE может поменять __main__ (--base), поэтому настраиваем мост при каждом вызове.
    bridgekit.configure(BASE)
    return bridgekit.call(path, payload, timeout)


def execs(*commands):
    return call("/exec", {"commands": list(commands)})


def be(x, y, z):
    return call(f"/block?x={x}&y={y}&z={z}").get("be", "")


def fluids(x, y, z):
    return call(f"/fluids?x={x}&y={y}&z={z}").get("tanks", [])


def snapshot():
    return {
        "generator": be(X, Y, Z),
        "wire": be(X + 1, Y, Z),
        "transformer": be(X + 2, Y, Z),
        "distiller_a": be(X + 3, Y, Z),
        "distiller_b": be(X + 4, Y, Z),
        "batbox": be(X + 5, Y, Z),
        "distiller_fluids": fluids(X + 3, Y, Z),
        "teleporter": be(*TELEPORTER),
        "reactor": be(*REACTOR),
        "macerator": be(*MACERATOR),
        "iron_furnace": be(*IRON_FURNACE),
        "solid_heat": be(*SOLID_HEAT),
        "stirling_ku": be(*STIRLING_KU),
        "wall": be(*WALL),
        "tank_fluids": fluids(*TANK_VALVE),
    }


def setup():
    execs(
        f"/forceload add {X - 8} {Z - 8} {X + 16} {Z + 8}",
        "/time set noon",
        "/gamerule doDaylightCycle false",
        f"/setblock {X} {Y} {Z} industrialreforged:generator",
        f"/setblock {X + 1} {Y} {Z} industrialreforged:wire_5_level",
        f"/setblock {X + 2} {Y} {Z} industrialreforged:molecular_transformer",
        f"/setblock {X + 3} {Y} {Z} industrialreforged:solar_distiller",
        f"/setblock {X + 4} {Y} {Z} industrialreforged:solar_distiller",
        f"/setblock {X + 5} {Y} {Z} industrialreforged:batbox",
        f'/data merge block {X} {Y} {Z} {{fuel:[{{Slot:0b,id:"minecraft:coal",count:12}}]}}',
        f'/data merge block {X + 1} {Y} {Z} {{energy:5000}}',
        f'/data merge block {X + 2} {Y} {Z} {{input:[{{Slot:0b,id:"minecraft:charcoal",count:6}}]}}',
        f'/data merge block {X + 5} {Y} {Z} {{charge:[{{Slot:0b,id:"industrialreforged:re_battery",count:1}}]}}',
        # телепортер с целью в Нижнем мире
        f"/setblock {TELEPORTER[0]} {TELEPORTER[1]} {TELEPORTER[2]} industrialreforged:teleporter",
        f'/data merge block {TELEPORTER[0]} {TELEPORTER[1]} {TELEPORTER[2]} '
        f'{{targetX:{X + 7},targetY:{Y},targetZ:{Z},targetDim:"minecraft:the_nether"}}',
        # реактор со стержнем и теплом
        f"/setblock {REACTOR[0]} {REACTOR[1]} {REACTOR[2]} industrialreforged:nuclear_reactor",
        f'/data merge block {REACTOR[0]} {REACTOR[1]} {REACTOR[2]} '
        f'{{reactor:[{{Slot:0b,id:"industrialreforged:uranium_fuel_rod",count:1}}],heat:4321}}',
        # дробилка с укреплением и рудой
        f"/setblock {MACERATOR[0]} {MACERATOR[1]} {MACERATOR[2]} industrialreforged:macerator_1_level",
        f'/data merge block {MACERATOR[0]} {MACERATOR[1]} {MACERATOR[2]} '
        f'{{upgrade:[{{Slot:0b,id:"industrialreforged:strengthening_upgrade",count:1}}],'
        f'input:[{{Slot:0b,id:"minecraft:iron_ore",count:7}}]}}',
        # мультиблок-резервуар 3x3x3 с водой
        f"/fill {X + 10} {Y} {Z - 1} {X + 12} {Y + 2} {Z + 1} industrialreforged:tank_wall",
        f"/fill {X + 11} {Y + 1} {Z} {X + 11} {Y + 1} {Z} minecraft:air",
        f"/setblock {TANK_VALVE[0]} {TANK_VALVE[1]} {TANK_VALVE[2]} industrialreforged:tank_valve[mode=water_in]",
        f"/setblock {TANK_CONTROLLER[0]} {TANK_CONTROLLER[1]} {TANK_CONTROLLER[2]} industrialreforged:tank_controller",
        # печь, теплогенератор, Стирлинг и стена: топливо и буферы должны пережить рестарт
        f"/setblock {IRON_FURNACE[0]} {IRON_FURNACE[1]} {IRON_FURNACE[2]} industrialreforged:iron_furnace",
        f'/data merge block {IRON_FURNACE[0]} {IRON_FURNACE[1]} {IRON_FURNACE[2]} '
        f'{{fuel:[{{Slot:0b,id:"minecraft:coal",count:3}}],burnTime:120,totalFuel:200}}',
        f"/setblock {SOLID_HEAT[0]} {SOLID_HEAT[1]} {SOLID_HEAT[2]} industrialreforged:solid_heat_generator",
        f'/data merge block {SOLID_HEAT[0]} {SOLID_HEAT[1]} {SOLID_HEAT[2]} '
        f'{{fuel:[{{Slot:0b,id:"minecraft:coal",count:2}}],burnTime:77,itemFuelTime:320}}',
        f"/setblock {STIRLING_KU[0]} {STIRLING_KU[1]} {STIRLING_KU[2]} industrialreforged:stirling_kinetic_generator",
        f'/data merge block {STIRLING_KU[0]} {STIRLING_KU[1]} {STIRLING_KU[2]} {{kUBuffer:1234,heatbuffer:500}}',
        # именно у obscured_wall есть block entity (обычные стены красятся блоком, без BE)
        f"/setblock {WALL[0]} {WALL[1]} {WALL[2]} industrialreforged:obscured_wall",
    )
    call("/wait", {"ticks": 40})
    call("/be", {"x": TANK_VALVE[0], "y": TANK_VALVE[1], "z": TANK_VALVE[2], "action": "validate"})
    call("/fluids", {"x": TANK_VALVE[0], "y": TANK_VALVE[1], "z": TANK_VALVE[2], "fluid": "minecraft:water", "amount": 4000})
    call("/fluids", {"x": X + 3, "y": Y, "z": Z, "fluid": "minecraft:water", "amount": 4000})
    call("/wait", {"ticks": 20})

    state = snapshot()
    with open(DUMP, "w", encoding="utf-8") as handle:
        json.dump(state, handle, indent=2)
    for key, value in state.items():
        print(f"{key}: {str(value)[:150]}")


def log_has_nbt_exception():
    """Ищет следы старого бага стены: CompoundTag.put(key, null) кидает исключение."""
    try:
        log_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "run-server", "logs", "latest.log")
        with open(log_path, encoding="utf-8", errors="replace") as handle:
            log = handle.read()
    except OSError:
        return False
    return "Invalid null NBT value" in log


def check():
    with open(DUMP, encoding="utf-8") as handle:
        before = json.load(handle)
    after = snapshot()

    for key in before:
        print(f"--- {key}")
        print(f"  до : {str(before[key])[:140]}")
        print(f"  после: {str(after[key])[:140]}")

    def fuel_count(snbt):
        match = re.search(r"fuel:\[\{Slot:0b,count:(\d+)", snbt)
        return int(match.group(1)) if match else 0

    def item_count(snbt, container):
        match = re.search(container + r":\[\{Slot:0b,count:(\d+),id:\"([^\"]+)\"", snbt)
        return (int(match.group(1)), match.group(2)) if match else (0, "")

    def tank_amount(entries):
        return sum(t.get("amount", 0) for t in entries)

    def int_field(snbt, key):
        match = re.search(r"(?<![A-Za-z_])" + key + r":(-?\d+)", snbt)
        return int(match.group(1)) if match else None

    checks = [
        ("генератор: уголь сохранился", fuel_count(after["generator"]) > 0, fuel_count(after["generator"])),
        ("MK: вход сохранился", item_count(after["transformer"], "input")[1] == "minecraft:charcoal",
         item_count(after["transformer"], "input")),
        ("МФЭХ(batbox): батарейка на месте", "re_battery" in after["batbox"], "re_battery" in after["batbox"]),
        ("дистиллятор: вода в общем буфере", tank_amount(after["distiller_fluids"]) > 0, after["distiller_fluids"]),
        ("телепортер: цель и измерение сохранились",
         'targetDim:"minecraft:the_nether"' in after["teleporter"] and "targetX:" in after["teleporter"],
         after["teleporter"][:100]),
        ("реактор: стержень и тепло сохранились",
         "uranium_fuel_rod" in after["reactor"] and "heat:4321" in after["reactor"], after["reactor"][:100]),
        ("дробилка: укрепление и руда сохранились",
         "strengthening_upgrade" in after["macerator"] and "iron_ore" in after["macerator"], after["macerator"][:100]),
        ("резервуар: вода пережила рестарт", tank_amount(after["tank_fluids"]) > 0, after["tank_fluids"]),
        # регресс сессии 35: счётчик горения и контейнер топлива делили ключ "fuel",
        # и один из них всегда затирал другой при сохранении
        ("печь: уголь в слоте топлива не исчез после рестарта",
         '"minecraft:coal"' in after["iron_furnace"], after["iron_furnace"][:120]),
        ("печь: счётчик горения лежит в burnTime (не в fuel)",
         int_field(after["iron_furnace"], "burnTime") is not None and "fuel:[{" in after["iron_furnace"],
         int_field(after["iron_furnace"], "burnTime")),
        # уголь в теплогенераторе к моменту проверки уже может догореть — важно, что
        # счётчик и знаменатель шкалы лежат в своих ключах и пережили рестарт
        ("теплогенератор: burnTime и itemFuelTime в своих ключах",
         int_field(after["solid_heat"], "burnTime") is not None
         and int_field(after["solid_heat"], "itemFuelTime") is not None,
         after["solid_heat"][:120]),
        ("Стирлинг: кинетический буфер пережил рестарт без изменений",
         int_field(after["stirling_ku"], "kUBuffer") == int_field(before["stirling_ku"], "kUBuffer")
         and int_field(after["stirling_ku"], "kUBuffer") is not None,
         after["stirling_ku"][:120]),
        # /block отдаёт SNBT block entity, а не id блока: у obscured_wall он "…:wall"
        ("стена с BE: синхронизация не бросает исключений",
         'id:"industrialreforged:wall"' in after["wall"] and not log_has_nbt_exception(),
         after["wall"][:80]),
    ]

    passed = 0
    for name, ok, detail in checks:
        print(f"{'PASS' if ok else 'FAIL'}  {name}  {detail}")
        passed += 1 if ok else 0
    print()
    print(f"итого: {passed}/{len(checks)} прошло")
    return 0 if passed == len(checks) else 1


def main():
    global BASE
    if "--base" in sys.argv:
        BASE = sys.argv[sys.argv.index("--base") + 1]
    phase = sys.argv[sys.argv.index("--phase") + 1] if "--phase" in sys.argv else "setup"

    if phase == "setup":
        setup()
        return 0
    return check()


if __name__ == "__main__":
    raise SystemExit(main())
