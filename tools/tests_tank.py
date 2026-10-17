#!/usr/bin/env python3
"""E2E-тесты мультиблока «Резервуар для пара и воды» (IC2 Classic style).

Проверяет:
1. Неполная коробка 3x3x3 -> ошибка валидации (formed=False).
2. Полноценная коробка 3x3x3 (1 полый блок -> емкость 16 000 мБ) -> успешная сборка.
3. Прием пара (steam) через FluidHandler клапана, проверка лимита 16 000 мБ.
4. Синхронизация общего хранилища между несколькими клапанами.
5. Автоматический распад структуры при разрушении блока обшивки tank_wall.
6. Масштабируемость: коробка 4x4x4 (2x2x2 = 8 блоков полости -> 128 000 мБ).

Usage: python3 tools/tests_tank.py
"""

import irbridge_testkit as t
from irbridge_testkit import block, be_field, check, execs, wait, be, be_set, fluids, fluid_fill, place, setblock


def build_box(min_pos, max_pos, casing_block="industrialreforged:tank_wall"):
    x1, y1, z1 = min_pos
    x2, y2, z2 = max_pos
    execs(f"fill {x1} {y1} {z1} {x2} {y2} {z2} {casing_block}")
    execs(f"fill {x1+1} {y1+1} {z1+1} {x2-1} {y2-1} {z2-1} minecraft:air")


def validate_tank(pos):
    return be_set(pos, action="validate")


def tank_amount(pos, index):
    """Резервуар-котёл хранит воду (0) и пар (1) раздельно."""
    tanks = fluids(pos).get("tanks", [])
    return tanks[index].get("amount", 0) if len(tanks) > index else 0


def main():
    execs("forceload add 270 -415 310 -385")
    execs("fill 270 150 -415 310 170 -385 minecraft:air")
    execs("fill 270 150 -415 310 150 -385 minecraft:iron_block")
    wait(10)

    # =========================================================================
    # 1. Неполная коробка 3x3x3 -> валидация должна отклонить структуру
    # =========================================================================
    min3 = (280, 151, -400)
    max3 = (282, 153, -398)
    build_box(min3, max3)

    valve1 = (281, 152, -398)
    place(valve1, "industrialreforged:tank_valve")

    # Делаем дыру в верхнем углу коробки
    hole_pos = (280, 153, -400)
    setblock(hole_pos, "minecraft:air")
    wait(5)

    res_fail = validate_tank(valve1)
    check("с дырой в обшивке структура не валидируется",
          res_fail.get("validated") is False and res_fail.get("formed") is False, res_fail)

    # =========================================================================
    # 2. Закрываем дыру -> успешная сборка коробки 3x3x3 (16 000 мБ)
    # =========================================================================
    setblock(hole_pos, "industrialreforged:tank_wall")
    wait(5)

    res_ok = validate_tank(valve1)
    check("полная коробка 3x3x3 валидируется успешно", res_ok.get("validated") is True, res_ok)
    check("емкость коробки 3x3x3 равна 16 000 мБ", res_ok.get("capacity") == 16000, res_ok.get("capacity"))

    # Клапан по умолчанию — «вход воды»; ключом доводим до «входа пара».
    # Порядок обхода: water_in -> water_out -> steam_in -> steam_out (2 клика).
    for _ in range(2):
        t.call("/use", {"x": valve1[0], "y": valve1[1], "z": valve1[2], "item": "industrialreforged:wrench", "player": "fake"})
    check("клапан переведён во вход пара", "mode=steam_in" in block(valve1).get("state", ""),
          block(valve1).get("state", ""))

    # =========================================================================
    # 3. Заливка пара в клапан (FluidHandler)
    # =========================================================================
    fill_res = fluid_fill(valve1, "industrialreforged:steam", 10000)
    wait(5)
    f_info = fluids(valve1)
    steam_amt = tank_amount(valve1, 1)
    check("клапан принял 10 000 мБ пара", steam_amt == 10000, f_info)

    # Доливаем еще 10 000 мБ (должно войти только 6 000 мБ до лимита 16 000)
    fluid_fill(valve1, "industrialreforged:steam", 10000)
    wait(5)
    steam_amt2 = tank_amount(valve1, 1)
    check("пар заполняет бак ровно до капа 16 000 мБ", steam_amt2 == 16000, steam_amt2)

    # =========================================================================
    # 4. Несколько клапанов делят единое мультиблочное хранилище
    # =========================================================================
    # Ставим второй клапан на противоположной стенке (north)
    valve2 = (281, 152, -400)
    place(valve2, "industrialreforged:tank_valve")
    wait(5)
    # Перевалидируем структуру через valve1
    validate_tank(valve1)
    wait(5)

    # Проверяем, что второй клапан видит те же 16 000 мБ пара из общего мастера
    v2_steam = tank_amount(valve2, 1)
    check("второй клапан видит ту же жидкость из общего танка", v2_steam == 16000, v2_steam)

    # =========================================================================
    # 5. Разрушение блока обшивки -> распад мультиблока
    # =========================================================================
    setblock(hole_pos, "minecraft:air")
    wait(5)
    v1_after_break = be(valve1).get("fields", {}).get("formed", {}).get("value", "true")
    # formed сбрасывается в false при разрушении
    check("при разрушении обшивки мультиблок распадается",
          v1_after_break == "false" or v1_after_break is False, v1_after_break)

    # =========================================================================
    # 6. Масштабируемость: коробка 4x4x4 (полость 2x2x2 = 8 -> 128 000 мБ)
    # =========================================================================
    min4 = (290, 151, -400)
    max4 = (293, 154, -397)
    build_box(min4, max4)

    valve4 = (291, 152, -397)
    place(valve4, "industrialreforged:tank_valve")
    wait(5)

    res4 = validate_tank(valve4)
    check("коробка 4x4x4 валидируется", res4.get("validated") is True, res4)
    check("емкость коробки 4x4x4 равна 128 000 мБ (8 блоков * 16k)", res4.get("capacity") == 128000, res4.get("capacity"))

    # =========================================================================
    # 7. Синхронизация уровня жидкости клиенту (визуализация воды/пара в резервуаре)
    # =========================================================================
    # Клапан, который валидировал структуру, становится мастером и держит воду/пар.
    # Клапан в режиме приёма не берёт чужую жидкость, поэтому для пара нужен steam_in.
    fluid_fill(valve4, "minecraft:water", 64000)
    wait(30)
    water_fill = be_field(valve4, "displayWaterFill")
    check("уровень воды синхронизирован мастеру (64 000 из 128 000 = 500 промилле)",
          int(water_fill) == 500, f"displayWaterFill={water_fill}")
    # Порядок обхода: water_in -> water_out -> steam_in -> steam_out (2 клика).
    for _ in range(2):
        t.call("/use", {"x": valve4[0], "y": valve4[1], "z": valve4[2],
                        "item": "industrialreforged:wrench", "player": "fake"})
    check("мастер переведён во вход пара", "mode=steam_in" in block(valve4).get("state", ""),
          block(valve4).get("state", ""))
    wait(5)
    fluid_fill(valve4, "industrialreforged:steam", 32000)
    wait(30)
    steam_fill = be_field(valve4, "displaySteamFill")
    water_fill2 = be_field(valve4, "displayWaterFill")
    check("уровень пара синхронизирован мастеру (32 000 из 128 000 = 250 промилле)",
          int(steam_fill) == 250, f"displaySteamFill={steam_fill}")
    check("уровень воды не изменился от добавления пара",
          int(water_fill2) == 500, f"displayWaterFill={water_fill2}")
    check("вода и пар в резервуаре (ёмкость 128 000 мБ)",
          tank_amount(valve4, 0) == 64000 and tank_amount(valve4, 1) == 32000,
          f"вода={tank_amount(valve4, 0)}, пар={tank_amount(valve4, 1)}")

    return t.summary("tests_tank")


if __name__ == "__main__":
    raise SystemExit(main())
