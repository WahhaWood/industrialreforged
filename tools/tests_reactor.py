#!/usr/bin/env python3
"""E2E-тесты ядерного реактора: камера, отвод тепла трубами (ядро и камеры), производство.

Usage: python3 tools/tests_reactor.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, be_field, be_set, break_block, energy, place

AREA = "forceload add 430 -420 470 -390"
CORE = (445, 100, -405)
CHAMBER = (446, 100, -405)
LONELY = (455, 100, -405)
PIPE_CORE = (444, 100, -405)
PIPE_CHAMBER = (447, 100, -405)
PIPE_LONELY = (454, 100, -405)


def main():
    execs(AREA)
    execs("fill 430 99 -420 470 99 -390 minecraft:iron_block")
    execs("fill 430 100 -420 470 100 -390 minecraft:air")
    wait(10)

    # ---- 1. камера без ядра остаётся на месте ---------------------------
    place(LONELY, "industrialreforged:reactor_chamber")
    wait(40)
    state = t.block(LONELY).get("state", "")
    check("камера вне ядра не ломается и не превращается в корпус", "reactor_chamber" in state, state)

    # ---- 2. камера вплотную к ядру расширяет активную зону --------------
    place(CORE, "industrialreforged:nuclear_reactor")
    place(CHAMBER, "industrialreforged:reactor_chamber")
    wait(20)
    size = int(be_field(CORE, "reactorSize") or 0)
    check("камера вплотную к ядру расширяет реактор (3 -> 4)", size == 4, f"reactorSize={size}")

    # ---- 3. после поломки камеры ядро снова 3 колонки -------------------
    break_block(CHAMBER)
    wait(20)
    size = int(be_field(CORE, "reactorSize") or 0)
    check("после поломки камеры ядро возвращается к 3 колонкам", size == 3, f"reactorSize={size}")

    # ---- 4. одиночная камера всё ещё цела --------------------------------
    state = t.block(LONELY).get("state", "")
    check("одиночная камера так и осталась на месте", "reactor_chamber" in state, state)

    # ---- 5. тепловая труба отводит тепло ядра ----------------------------
    place(PIPE_CORE, "industrialreforged:heat_pipe_1")
    wait(5)
    pipe_state = t.block(PIPE_CORE).get("state", "")
    check("труба соединяется с ядром реактора (east)", "east=true" in pipe_state, pipe_state)
    be_set(CORE, setField="heat", value=5000)
    wait(80)
    pipe_heat = int(be_field(PIPE_CORE, "heat"))
    core_heat = int(be_field(CORE, "heat"))
    check("труба тянет тепло из ядра", pipe_heat > 0 and core_heat < 5000,
          f"труба {pipe_heat}, реактор {core_heat}")

    # ---- 6. камера в структуре: труба цепляется к камере и тянет тепло ядра
    place(CHAMBER, "industrialreforged:reactor_chamber")
    place(PIPE_CHAMBER, "industrialreforged:heat_pipe_1")
    wait(10)
    pipe_state = t.block(PIPE_CHAMBER).get("state", "")
    check("труба соединяется с камерой в структуре (west)", "west=true" in pipe_state, pipe_state)
    be_set(CORE, setField="heat", value=5000)
    wait(80)
    pipe_heat = int(be_field(PIPE_CHAMBER, "heat"))
    core_heat = int(be_field(CORE, "heat"))
    check("труба у камеры тянет тепло ядра", pipe_heat > 0 and core_heat < 5000,
          f"труба {pipe_heat}, реактор {core_heat}")

    # ---- 7. одиночная камера к трубе не подключается ---------------------
    place(PIPE_LONELY, "industrialreforged:heat_pipe_1")
    wait(10)
    pipe_state = t.block(PIPE_LONELY).get("state", "")
    check("труба НЕ соединяется с одиночной камерой (east=false)", "east=true" not in pipe_state, pipe_state)

    # ---- 8. реактор производит энергию -----------------------------------
    be_set(CORE, setField="reactorSlot", slot=0, item="industrialreforged:uranium_fuel_rod", count=1)
    place((CORE[0], CORE[1] + 1, CORE[2]), "minecraft:redstone_block")
    wait(80)
    stored = int(energy(CORE).get("stored", 0))
    output = float(be_field(CORE, "output") or 0)
    check("реактор производит энергию (1 стержень ≈ 500 FE за цикл)", stored >= 500 and output > 0,
          f"энергия {stored}, импульсов/с {output}")

    # ---- 9. нагрев одиночного стержня совпадает с тултипом (~4/сек) ------
    SOLO = (460, 100, -405)
    place(SOLO, "industrialreforged:nuclear_reactor")
    wait(5)
    be_set(SOLO, setField="reactorSlot", slot=0, item="industrialreforged:uranium_fuel_rod", count=1)
    place((SOLO[0], SOLO[1] + 1, SOLO[2]), "minecraft:redstone_block")
    wait(65)
    heat = int(be_field(SOLO, "heat") or 0)
    check("одиночный стержень греет ~4/сек (как в тултипе)", 4 <= heat <= 16, f"тепло {heat} за ~3 цикла")

    # ---- 10. плутониевый стержень мощнее уранового -----------------------
    PLUTO = (465, 100, -405)
    place(PLUTO, "industrialreforged:nuclear_reactor")
    wait(5)
    be_set(PLUTO, setField="reactorSlot", slot=0, item="industrialreforged:plutonium_fuel_rod", count=1)
    place((PLUTO[0], PLUTO[1] + 1, PLUTO[2]), "minecraft:redstone_block")
    wait(80)
    pluto_energy = int(energy(PLUTO).get("stored", 0))
    check("плутониевый стержень даёт ~2× энергии урана", pluto_energy >= stored * 1.5 and pluto_energy > 0,
          f"плутоний {pluto_energy} FE, уран {stored} FE")

    # ---- 11. литиевый стержень нарабатывается в тритиевый ----------------
    TRIT = (470, 100, -405)
    place(TRIT, "industrialreforged:nuclear_reactor")
    wait(5)
    be_set(TRIT, setField="reactorSlot", slot=0, item="industrialreforged:uranium_fuel_rod", count=1)
    be_set(TRIT, setField="reactorSlot", slot=1, item="industrialreforged:lithium_fuel_rod", count=1, damage=9999)
    be_set(TRIT, setField="heat", value=6000)
    place((TRIT[0], TRIT[1] + 1, TRIT[2]), "minecraft:redstone_block")
    wait(80)
    slots = t.be(TRIT).get("fields", {}).get("reactorSlot", {}).get("slots", [])
    bred = next((s.get("item", "") for s in slots if s.get("slot") == 1), "")
    check("литиевый стержень в реакторе превращается в тритиевый", "tritium_fuel_rod" in bred, bred)

    return t.summary("tests_reactor")


if __name__ == "__main__":
    raise SystemExit(main())
