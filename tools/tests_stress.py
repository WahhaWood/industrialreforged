#!/usr/bin/env python3
"""Стресс-тест производительности: большой резервуар, длинная сеть тепловых труб,
много машин; замер времени валидации и TPS сервера под нагрузкой.

Usage: python3 tools/tests_stress.py --base http://127.0.0.1:9878
"""

import sys
import time

import irbridge_testkit as t
from irbridge_testkit import check, execs, place, wait

AREA = "forceload add 540 -800 700 -660"
FLOOR_Y = 99
BASE_Y = 100
TANK = (600, BASE_Y, -700)          # угол 9x9x9
PIPE_X, PIPE_Y, PIPE_Z = 600, BASE_Y, -740
PIPE_LEN = 200
MACHINES = 20


def measure_tps(ticks=200):
    started = time.time()
    wait(ticks)
    elapsed = time.time() - started
    return ticks / elapsed if elapsed > 0 else 0.0


def main():
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]

    execs(AREA)
    execs(f"fill 540 {FLOOR_Y} -800 700 {FLOOR_Y} -660 minecraft:iron_block")
    execs(f"fill 540 {BASE_Y} -800 700 {BASE_Y} -660 minecraft:air")
    wait(10)

    # ---- 1. базовый TPS без нагрузки -------------------------------------
    baseline = measure_tps(100)
    check("базовый TPS ~20", baseline >= 18.0, f"{baseline:.1f} TPS")

    # ---- 2. резервуар 9x9x9: валидация и её время ------------------------
    x, y, z = TANK
    execs(f"fill {x} {y} {z} {x + 8} {y + 8} {z + 8} industrialreforged:tank_wall")
    execs(f"fill {x + 1} {y + 1} {z + 1} {x + 7} {y + 7} {z + 7} minecraft:air")
    execs(f"setblock {x + 4} {y} {z} industrialreforged:tank_valve[mode=water_in]")
    wait(5)
    started = time.time()
    result = t.be_set((x + 4, y, z), action="validate")
    validate_time = time.time() - started
    check("резервуар 9x9x9 валидируется", result.get("formed") is True, result)
    check("валидация 9x9x9 быстрее 1 секунды", validate_time < 1.0, f"{validate_time:.3f} c")

    # ---- 3. длинная сеть тепловых труб + машины --------------------------
    execs(f"fill {PIPE_X} {PIPE_Y} {PIPE_Z} {PIPE_X + PIPE_LEN - 1} {PIPE_Y} {PIPE_Z} industrialreforged:heat_pipe_3")
    machine_positions = []
    for i in range(MACHINES):
        pos = (PIPE_X + 5 + i * 9, PIPE_Y + 1, PIPE_Z)
        machine_positions.append(pos)
        place(pos, "industrialreforged:macerator_4_level")
        t.be_set(pos, setField="machineHeat", value=8000)
        t.be_set(pos, setField="inputSlot", slot=0, item="minecraft:iron_ore", count=64)
        t.energy_set(pos, 10_000_000)
    wait(60)

    # 200 труб 3 уровня рассеивают 6400 тепла/тик, поэтому сеть не накапливает тепло —
    # проверяем сам отвод: машины остыли быстрее пассивного −1/тик
    heat_left = sum(int(t.be_field(pos, "machineHeat") or 0) for pos in machine_positions)
    check("сеть труб вытянула тепло из машин", heat_left < MACHINES * 8000 - 20000,
          f"осталось {heat_left} из {MACHINES * 8000}")

    # ---- 4. TPS под нагрузкой --------------------------------------------
    loaded = measure_tps(200)
    check("TPS под нагрузкой (20 машин + 200 труб + резервуар) >= 15", loaded >= 15.0, f"{loaded:.1f} TPS")

    return t.summary("tests_stress")


if __name__ == "__main__":
    raise SystemExit(main())
