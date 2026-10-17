#!/usr/bin/env python3
"""E2E-тест свойства FACING клапана резервуара.

FACING добавлен, чтобы портовая грань была одна, а не на всех шести, и чтобы
стрелки «вход/выход» читались. Тест проверяет, что это не сломало мультиблок:

1. Дефолтное состояние клапана — facing=north (иначе blockstate без варианта).
2. blockstate принимает все 6 значений facing.
3. Ключ (wrench) циклит РЕЖИМ и не сбрасывает FACING.
4. Валидация работает при любом FACING и зажигает active.
5. Разрушение обшивки гасит клапан независимо от FACING.

Запуск (порт моста можно переопределить, если 9878 занят чужим проектом):
    python3 tools/tests_tank_valve_facing.py
    IRBRIDGE_PORT=9879 python3 tools/tests_tank_valve_facing.py
"""

import irbridge_testkit as t
from irbridge_testkit import block, break_block, check, execs, wait, place, setblock

FACINGS = ["north", "east", "south", "west", "up", "down"]
# Порядок обхода ключом, как объявлено в TankValveBlock.TankValveMode:
# вход воды -> выход воды -> вход пара -> выход пара.
MODES = ["water_in", "water_out", "steam_in", "steam_out"]


def state_of(pos):
    """Строка BlockState из /block: 'Block{mod:valve}[active=false,facing=north,mode=water_in]'."""
    return block(pos).get("state", "")


def field(pos, key):
    """Значение одного свойства состояния блока, например ('active', 'false')."""
    state = state_of(pos)
    if "[" in state:
        state = state[state.index("[") + 1:]
    for part in state.rstrip("]").split(","):
        name, sep, value = part.partition("=")
        if sep and name.strip() == key:
            return value.strip()
    return None


def build_box(min_pos, max_pos):
    """Коробка из tank_wall с вып hollow полостью — рабочая структура 3x3x3."""
    x1, y1, z1 = min_pos
    x2, y2, z2 = max_pos
    execs(f"fill {x1} {y1} {z1} {x2} {y2} {z2} industrialreforged:tank_wall")
    execs(f"fill {x1+1} {y1+1} {z1+1} {x2-1} {y2-1} {z2-1} minecraft:air")


def cycle_mode(valve, times=1):
    for _ in range(times):
        t.call("/use", {"x": valve[0], "y": valve[1], "z": valve[2],
                        "item": "industrialreforged:wrench", "player": "fake"})
    wait(4)


def main():
    execs("forceload add 330 -415 370 -385")
    execs("fill 330 150 -415 370 170 -385 minecraft:air")
    execs("fill 330 150 -415 370 150 -385 minecraft:iron_block")
    wait(10)

    # Свободная позиция для проверок без мультиблока.
    d = (335, 152, -398)
    place(d, "industrialreforged:tank_valve")
    check("клапан по умолчанию смотрит на север", field(d, "facing") == "north", state_of(d))

    # =====================================================================
    # 1. Все шесть граней принимаются blockstate'ом.
    # =====================================================================
    bad = []
    for f in FACINGS:
        setblock(d, "industrialreforged:tank_valve", f"facing={f},mode=water_out")
        if field(d, "facing") != f:
            bad.append(f"{f}->{field(d, 'facing')}")
    check("blockstate принимает все 6 значений facing", not bad, ", ".join(bad))

    # =====================================================================
    # 2. Режим и facing независимы: смена режима не сбрасывает facing.
    # =====================================================================
    setblock(d, "industrialreforged:tank_valve", "facing=west,mode=water_in")
    cycle_mode(d)
    # water_in -> water_out по порядку enum
    check("ключ сменил режим, сохранив facing=west",
          field(d, "facing") == "west" and field(d, "mode") == "water_out", state_of(d))

    # Полный обход ровно по MODES и возвращение в исходный режим.
    # Стартуем с первого режима по кругу, иначе проверка зависит от того,
    # сколько раз ключ уже щёлкнули выше по тесту.
    setblock(d, "industrialreforged:tank_valve", f"facing=west,mode={MODES[0]}")
    walked = [field(d, "mode")]
    for _ in range(len(MODES)):
        cycle_mode(d)
        walked.append(field(d, "mode"))
    check("порядок обхода: вход воды -> выход воды -> вход пара -> выход пара",
          walked == MODES + [MODES[0]], walked)
    check("перебор режимов по кругу вернул исходный",
          field(d, "mode") == MODES[0], walked)
    check("facing=west пережил полный перебор режимов",
          field(d, "facing") == "west", state_of(d))

    # =====================================================================
    # 3. Валидация работает при любом facing.
    #    Клапан ставится прямо на место стенки (угол обшивки).
    # =====================================================================
    box = ((340, 151, -402), (342, 153, -400))
    valve = (342, 152, -400)          # угол обшивки, z2 — грань +z
    results = {}
    for f in FACINGS:
        build_box(*box)
        place(valve, "industrialreforged:tank_valve", f"facing={f},mode=water_in")
        wait(8)
        results[f] = (t.be_set(valve, action="validate"), state_of(valve))

    not_formed = {f: r.get("validated") for f, (r, _) in results.items()
                  if not (r.get("validated") and r.get("formed"))}
    check("мультиблок собирается при любом facing", not not_formed,
          "; ".join(f"{f}:validated={v}" for f, v in not_formed.items()))

    unlit = [f for f, (_, st) in results.items() if "active=true" not in st]
    check("клапан подсвечен при любом facing", not unlit, f"погашены: {unlit}")

    # Ёмкость не зависит от facing — структура та же самая.
    caps = {r.get("capacity") for r, _ in results.values()}
    check("ёмкость одинакова при любом facing", len(caps) == 1, f"ёмкости: {caps}")

    # =====================================================================
    # 4. Разрушение обшивки гасит клапан независимо от facing.
    # =====================================================================
    build_box(*box)
    place(valve, "industrialreforged:tank_valve", "facing=east,mode=water_in")
    wait(8)
    t.be_set(valve, action="validate")
    check("клапан facing=east подсвечен после сборки",
          field(valve, "active") == "true", state_of(valve))

    break_block((340, 152, -402))
    wait(10)
    check("посломки обшивки гасят клапан",
          field(valve, "active") == "false", state_of(valve))

    return t.summary("tests_tank_valve_facing")


if __name__ == "__main__":
    raise SystemExit(main())
