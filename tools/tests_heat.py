#!/usr/bin/env python3
"""E2E-тесты тепла машин: рост, замедление, перегрев, трубы, охладители.

Usage: python3 tools/tests_heat.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, be_field, be_set, energy, energy_set, place, clear_container

AREA = "forceload add 230 -420 320 -380"
FURNACE = (240, 100, -400)
PIPE = (241, 100, -400)
FURNACE2 = (250, 100, -400)
PIPE2 = (251, 100, -400)


def progress_delta(pos, ticks):
    before = int(be_field(pos, "guiProgress"))
    wait(ticks)
    after = int(be_field(pos, "guiProgress"))
    return after - before


def fresh_machine(pos):
    place(pos, "industrialreforged:macerator_4_level")
    wait(5)
    clear_container(pos, "inputSlot", 4)
    clear_container(pos, "outputSlot", 4)
    energy_set(pos, 10_000_000)


def main():
    execs(AREA)
    execs("fill 230 99 -420 320 -380 minecraft:iron_block")
    execs("fill 230 100 -420 320 -380 minecraft:air")
    # убираем трубы прошлых прогонов: иначе они дренируют тепло машин
    execs("fill 241 100 -400 242 100 -400 minecraft:air")
    execs("fill 251 100 -400 252 100 -400 minecraft:air")
    wait(20)

    # ---- 1. +500 тепла за завершённый рецепт -----------------------------
    fresh_machine(FURNACE)
    be_set(FURNACE, setField="machineHeat", value=0)
    be_set(FURNACE, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=64)
    wait(10)
    before = int(be_field(FURNACE, "machineHeat"))
    # рецепт мацератора 4 уровня — ~345 тиков: к этому моменту рецепт завершается и даёт +500
    wait(380)
    after = int(be_field(FURNACE, "machineHeat"))
    check("тепло +500 за завершённый рецепт", 380 <= after <= 520, f"{before} -> {after} (ожидали ~500)")

    # ---- 2. тепло падает в простое --------------------------------------
    clear_container(FURNACE, "inputSlot", 4)
    energy_set(FURNACE, 0)
    be_set(FURNACE, setField="machineHeat", value=3000)
    wait(120)
    cooled = int(be_field(FURNACE, "machineHeat"))
    check("тепло падает без работы", cooled < 3000, f"через 120 тиков: {cooled}")

    # ---- 3. замедление на жаре ------------------------------------------
    be_set(FURNACE, setField="machineHeat", value=0)
    be_set(FURNACE, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=64)
    energy_set(FURNACE, 10_000_000)
    wait(20)
    cold_start = energy(FURNACE)
    cold_start_val = float(cold_start.get("storedLong", cold_start.get("stored")))
    wait(200)
    cold_end = energy(FURNACE)
    cold_end_val = float(cold_end.get("storedLong", cold_end.get("stored")))
    cold_spent = cold_start_val - cold_end_val

    be_set(FURNACE, setField="machineHeat", value=8000)
    energy_set(FURNACE, 10_000_000)
    wait(20)
    hot_start = energy(FURNACE)
    hot_start_val = float(hot_start.get("storedLong", hot_start.get("stored")))
    wait(200)
    hot_end = energy(FURNACE)
    hot_end_val = float(hot_end.get("storedLong", hot_end.get("stored")))
    hot_spent = hot_start_val - hot_end_val

    ratio = hot_spent / cold_spent if cold_spent > 0 else -1.0
    check("на жаре машина ест энергию медленнее (замедление)", 0.35 <= ratio <= 0.85, f"горячая/холодная = {ratio:.2f}")

    # ---- 4. перегрев: стоп и остывание ----------------------------------
    be_set(FURNACE, setField="machineHeat", value=10000)
    wait(10)
    overheated = be_field(FURNACE, "overheated")
    check("при 10000 тепла включается флаг перегрева", overheated == "true", overheated)
    frozen = progress_delta(FURNACE, 60)
    check("перегретая машина не работает", frozen == 0, f"прогресс сдвинулся на {frozen}")
    be_set(FURNACE, setField="machineHeat", value=4000)
    wait(40)
    resumed = progress_delta(FURNACE, 60)
    check("после охлаждения до 4000 машина возобновляет работу", resumed > 0, f"прогресс {resumed}")

    # ---- 5. труба тянет тепло, сеть рассеивает его в воздух --------------
    place(PIPE, "industrialreforged:heat_pipe_1")
    wait(5)
    pipe_state = t.block(PIPE).get("state", "")
    check("труба соединяется с машиной (west)", "west=true" in pipe_state, pipe_state)
    clear_container(FURNACE, "inputSlot", 4)
    energy_set(FURNACE, 0)
    be_set(FURNACE, setField="machineHeat", value=8000)
    wait(200)
    furnace_heat = int(be_field(FURNACE, "machineHeat"))
    pipe_heat = int(be_field(PIPE, "heat"))
    check("труба ускоряет охлаждение машины", furnace_heat < 7000,
          f"8000 -> {furnace_heat} за 200 тиков (пассивно было бы ~7800)")
    check("тепло ушло в сеть труб", pipe_heat > 0, f"в трубе {pipe_heat}")
    wait(1200)
    furnace_heat = int(be_field(FURNACE, "machineHeat"))
    pipe_heat = int(be_field(PIPE, "heat"))
    check("сеть труб сама рассеивает тепло в воздух", furnace_heat == 0 and pipe_heat == 0,
          f"машина {furnace_heat}, труба {pipe_heat}")

    # ---- 6. трубы не создают тепло из ничего ----------------------------
    place(FURNACE2, "industrialreforged:macerator_4_level")
    place(PIPE2, "industrialreforged:heat_pipe_2")
    wait(5)
    energy_set(FURNACE2, 0)
    be_set(FURNACE2, setField="machineHeat", value=5000)
    wait(600)
    pipe2_heat = int(be_field(PIPE2, "heat"))
    machine_left = int(be_field(FURNACE2, "machineHeat"))
    check("тепло сохраняется (труба не генерирует)", machine_left + pipe2_heat <= 5100,
          f"машина {machine_left} + труба {pipe2_heat} > 5000 + запас")

    return t.summary("tests_heat")


if __name__ == "__main__":
    raise SystemExit(main())
