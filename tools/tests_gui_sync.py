#!/usr/bin/env python3
"""Проверка синхронизации GUI: что экран реально увидит, а не что есть на сервере.

Зачем. Шесть значений в GUI машин выглядели «живыми» на сервере, но до клиента не
доезжали: серверная копия block entity всегда права, клиентская оставалась нулевой,
и экран рисовал пустоту. Ни в логе, ни в тестах на выделенном сервере это не видно —
нужен именно клиент. Тест ставит машины в РАБОТОЕ состояние (иначе все значения
нулевые и проверять нечего), открывает их меню и читает поля клиентской копии
через эндпоинт `/clientbe`.

Требует запущенного клиента (runClient). На выделенном сервере все проверки SKIP.

Использование: python3 tools/tests_gui_sync.py [--base http://127.0.0.1:9877]
"""

import re
import sys
import time

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, call

X, Y, Z = 240, 88, -300


def client_fields(pos):
    """Поля клиентской копии block entity: {имя: значение}."""
    raw = call(f"/clientbe?x={pos[0]}&y={pos[1]}&z={pos[2]}").get("nbt", "")
    fields = {}
    for part in raw.split("fields=")[-1].split(", "):
        if "=" in part:
            name, value = part.split("=", 1)
            fields[name.strip()] = value.strip()
    return raw, fields


def open_menu(pos):
    call("/player", {"player": "real", "x": pos[0] + 0.5, "y": pos[1], "z": pos[2] + 1.5})
    call("/menu/open", {"x": pos[0], "y": pos[1], "z": pos[2]})
    time.sleep(1.5)


def close_menu():
    call("/menu/close", {})
    time.sleep(0.4)


def main():
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]
    alive, info = t.alive()
    if not alive:
        print(f"SKIP: нет клиента ({info}) — проверять синхронизацию нечем")
        return 0
    # Набор работает с клиентской копией block entity, поэтому на выделенном сервере
    # он неприменим. Раньше здесь стояла путаная проверка, и набор не пропускался, а
    # падал всеми шестью проверками — выглядело как регресс синхронизации.
    dist = call("/status").get("dist", "")
    if dist != "CLIENT":
        print(f"SKIP: нужен клиент, поднят {dist} — клиентской копии block entity нет")
        return 0

    execs("/time set noon", "/gamerule doDaylightCycle false", "/weather clear",
          f"forceload add {X - 10} {Z - 10} {X + 14} {Z + 10}",
          f"fill {X - 6} {Y - 1} {Z - 6} {X + 12} {Y - 1} {Z + 6} minecraft:stone",
          f"fill {X - 6} {Y} {Z - 6} {X + 12} {Y + 4} {Z + 6} minecraft:air")

    # 1. теплогенератор: горит, чтобы шкала топлива была не нулевой
    heat_gen = (X, Y, Z)
    execs(f"setblock {X} {Y} {Z} industrialreforged:solid_heat_generator",
          'data merge block ' + f"{X} {Y} {Z} " + '{fuel:[{Slot:0b,id:"minecraft:coal",count:8}],'
          'burnTime:600,itemFuelTime:1600}')
    wait(20)
    open_menu(heat_gen)
    raw, fields = client_fields(heat_gen)
    check("теплогенератор: топливо доехало до клиента",
          int(fields.get("fuel", 0) or 0) > 0 and int(fields.get("itemFuelTime", 0) or 0) > 0,
          f"fuel={fields.get('fuel')} itemFuelTime={fields.get('itemFuelTime')}")
    close_menu()

    # 2. Стирлиг-кинетик: жидкость в танке + кинетический буфер
    stirling_ku = (X + 2, Y, Z)
    execs(f"setblock {X + 2} {Y} {Z} industrialreforged:stirling_kinetic_generator",
          f"data merge block {X + 2} {Y} {Z} " + "{power:900,kUBuffer:900}")
    call("/fluids", {"x": X + 2, "y": Y, "z": Z, "tank": 0,
                     "fluid": "minecraft:water", "amount": 4000})
    wait(20)
    open_menu(stirling_ku)
    raw, fields = client_fields(stirling_ku)
    check("Стирлиг-кинетик: бак с жидкостью виден клиенту", "4000" in raw or "amount" in raw, raw[-160:])
    close_menu()

    # 3. Стирлиг-генератор (FE): нужен источник тепла перед ним
    stirling_fe = (X + 4, Y, Z)
    execs(f"setblock {X + 4} {Y} {Z} industrialreforged:stirling_generator[facing=north]",
          f"setblock {X + 4} {Y} {Z - 1} industrialreforged:solid_heat_generator[facing=south]",
          'data merge block ' + f"{X + 4} {Y} {Z - 1} " + '{fuel:[{Slot:0b,id:"minecraft:coal",count:8}],'
          'burnTime:1600,itemFuelTime:1600}',
          f"data merge block {X + 4} {Y} {Z} " + "{energy:20000}")
    wait(60)
    open_menu(stirling_fe)
    raw, fields = client_fields(stirling_fe)
    server_production = 0
    try:
        server_production = int(call("/be", {"x": X + 4, "y": Y, "z": Z})
                                .get("fields", {}).get("production", {}).get("value", 0))
    except Exception:
        pass
    client_production = int(fields.get("production", 0) or 0)
    check("Стирлиг-генератор: production доехал до клиента",
          client_production == server_production and server_production > 0,
          f"сервер={server_production} клиент={client_production}")
    close_menu()

    # 4. индукционная печь: работает на железе
    furnace = (X + 6, Y, Z)
    execs(f"setblock {X + 6} {Y} {Z} industrialreforged:induction_furnace",
          f"data merge block {X + 6} {Y} {Z} " + "{energy:500000}",
          'data merge block ' + f"{X + 6} {Y} {Z} " + '{input:[{Slot:0b,id:"minecraft:iron_ore",count:8}]}')
    wait(40)
    open_menu(furnace)
    raw, fields = client_fields(furnace)
    check("индукционная печь: прогресс виден клиенту", int(fields.get("progress", 0) or 0) > 0,
          f"progress={fields.get('progress')} heat={fields.get('heat')}")
    close_menu()

    # 5. зарядная плита: переключаем режим и ждём, чтобы клиент обновился
    pad = (X + 8, Y, Z)
    execs(f"setblock {X + 8} {Y} {Z} industrialreforged:efsu_chargepad",
          f"data merge block {X + 8} {Y} {Z} " + "{energy:700000,redstoneMode:1b}")
    wait(20)
    open_menu(pad)
    raw, fields = client_fields(pad)
    check("зарядная плита: режим редстоуна виден клиенту", int(fields.get("redstoneMode", -1) or -1) == 1,
          f"redstoneMode={fields.get('redstoneMode')}")
    close_menu()

    # 6. солнечный дистиллятор: вода в баке + солнце -> полоса «солнце в зените»
    distiller = (X - 2, Y, Z)
    execs(f"setblock {X - 2} {Y} {Z} industrialreforged:solar_distiller")
    call("/fluids", {"x": X - 2, "y": Y, "z": Z, "fluid": "minecraft:water", "amount": 4000})
    wait(40)
    open_menu(distiller)
    raw, fields = client_fields(distiller)
    sky = float(fields.get("skyLight", 0) or 0)
    has_water = "input_tank" in raw and "water" in raw
    check("дистиллятор: солнечный свет виден клиенту", sky > 0.5, f"skyLight={sky} вода={has_water}")
    close_menu()

    return t.summary("tests_gui_sync")


if __name__ == "__main__":
    raise SystemExit(main())
