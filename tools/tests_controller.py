#!/usr/bin/env python3
"""E2E: контроллер резервуара — часть оболочки, зеркалит воду/пар/тепло и открывает GUI.

Проверяет:
1. Сборку танка через контроллер (мастер — клапан структуры).
2. Подсветку active=true у контроллера и клапана.
3. Зеркало контроллера: вода/пар/тепло/ёмкость/статус как у мастера.
4. Открытие меню контроллера (MenuProvider).
5. Поломку стенки: танк и контроллер распускаются.
6. Пересборку через клапан: контроллер снова подхватывается.

Usage: python3 tools/tests_controller.py
"""

import irbridge_testkit as t
from irbridge_testkit import be_field, be_set, block, break_block, check, execs, fluid_fill, place, wait


def build_box(min_pos, max_pos, casing="industrialreforged:tank_wall"):
    x1, y1, z1 = min_pos
    x2, y2, z2 = max_pos
    execs(f"fill {x1} {y1} {z1} {x2} {y2} {z2} {casing}")
    execs(f"fill {x1+1} {y1+1} {z1+1} {x2-1} {y2-1} {z2-1} minecraft:air")


def main():
    execs("forceload add 395 -415 425 -395")
    execs("fill 395 150 -415 425 160 -395 minecraft:air")
    execs("fill 395 150 -415 425 150 -395 minecraft:iron_block")
    wait(10)

    # Танк 3x3x3: клапан и контроллер на южной стенке (оба смотрят в полость)
    build_box((400, 151, -410), (402, 153, -408))
    valve = (401, 152, -408)
    controller = (402, 152, -408)
    place(valve, "industrialreforged:tank_valve")
    place(controller, "industrialreforged:tank_controller")
    wait(5)

    # ---- 1. сборка через контроллер --------------------------------------
    res = be_set(controller, action="validate")
    check("контроллер собрал танк", res.get("validated") is True, res)
    check("ёмкость 3x3x3 = 16 000 мБ", res.get("capacity") == 16000, res.get("capacity"))
    check("контроллер подсвечен active=true", "active=true" in block(controller).get("state", ""), block(controller).get("state", ""))
    check("клапан подсвечен active=true", "active=true" in block(valve).get("state", ""), block(valve).get("state", ""))

    # ---- 2. зеркало контроллера: вода + тепло → пар ----------------------
    fluid_fill(valve, "minecraft:water", 1000)
    wait(5)
    be_set(valve, setField="heat", value=1000)
    wait(10)
    check("контроллер видит воду (990)", int(be_field(controller, "displayWater")) == 990, be_field(controller, "displayWater"))
    check("контроллер видит пар (1000)", int(be_field(controller, "displaySteam")) == 1000, be_field(controller, "displaySteam"))
    check("контроллер видит остаток тепла", int(be_field(controller, "displayHeat")) <= 100, be_field(controller, "displayHeat"))
    check("контроллер видит ёмкость 16 000", int(be_field(controller, "displayCapacity")) == 16000, be_field(controller, "displayCapacity"))
    check("контроллер считает структуру собранной", int(be_field(controller, "displayFormed")) == 1, be_field(controller, "displayFormed"))

    # ---- 3. меню контроллера открывается ---------------------------------
    menu = t.call("/menu/open", {"x": controller[0], "y": controller[1], "z": controller[2], "player": "fake"})
    check("меню контроллера открывается", menu.get("ok") is True, menu)
    check("в меню нет слотов (панель состояния)", menu.get("slotCount") == 0, menu.get("slotCount"))

    # ---- 4. поломка стенки распускает танк и контроллер ------------------
    break_block((400, 152, -409))
    wait(5)
    check("после поломки контроллер погас", "active=false" in block(controller).get("state", ""), block(controller).get("state", ""))
    check("после поломки контроллер распущен", int(be_field(controller, "displayFormed")) == 0, be_field(controller, "displayFormed"))

    # ---- 5. пересборка через клапан подхватывает контроллер --------------
    execs("setblock 400 152 -409 industrialreforged:tank_wall")
    wait(5)
    res2 = be_set(valve, action="validate")
    check("клапан пересобрал танк", res2.get("validated") is True, res2)
    wait(5)
    check("контроллер снова подсвечен", "active=true" in block(controller).get("state", ""), block(controller).get("state", ""))
    check("контроллер снова видит структуру", int(be_field(controller, "displayFormed")) == 1, be_field(controller, "displayFormed"))

    # ---- 6. контроллер сам по себе не оболочка-клапан --------------------
    # (танк без клапана собрать нельзя — проверяем сообщение об ошибке)
    build_box((410, 151, -410), (412, 153, -408))
    lonely = (411, 152, -408)
    place(lonely, "industrialreforged:tank_controller")
    wait(5)
    res3 = be_set(lonely, action="validate")
    check("танк без клапана не собирается", res3.get("validated") is False, res3)
    check("контроллер без танка не подсвечен", "active=false" in block(lonely).get("state", ""), block(lonely).get("state", ""))

    return t.summary("tests_controller")


if __name__ == "__main__":
    raise SystemExit(main())
