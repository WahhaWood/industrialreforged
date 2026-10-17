#!/usr/bin/env python3
"""E2E-тест переноса предметов: эжектор, затягиватель, внутренняя сортировка.

Покрывает Block C: экспорт готового в сундук, скорость от числа в стаке,
возврат сырья из выхода во вход (а не вывоз непереработанного), отсутствие
петли «продукт -> вход», затягивание из сундука, веер по сторонам без голодания,
запрет выгребания через верх (UP insert-only).

Usage: IRBRIDGE_PORT=9879 python3 tools/tests_transfer.py
"""

import re

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, be_set, energy_set, place, clear_container

AREA = "forceload add 230 -380 280 -350"
MAC1 = (250, 210, -365)
MAC2 = (260, 210, -365)
MAC3 = (270, 210, -365)
ORE = "minecraft:iron_ore"
DUST = "industrialreforged:crushed_iron"
EJECT = "industrialreforged:ejector_upgrade"
PULL = "industrialreforged:pulling_upgrade"


def field_slots(pos, field):
    entry = t.be(pos).get("fields", {}).get(field, {})
    return entry.get("slots", []) if isinstance(entry, dict) else []


def count_of(pos, field, item):
    return sum(int(s.get("count", 0)) for s in field_slots(pos, field) if s.get("item") == item)


def chest_count(ch, item):
    out = " ".join(t.execs(f"data get block {ch[0]} {ch[1]} {ch[2]} Items").get("output", []))
    ids = re.findall(r'id:\s*"([^"]+)"', out)
    counts = [int(v) for v in re.findall(r'[Cc]ount:\s*(\d+)', out)]
    return sum(c for i, c in zip(ids, counts) if i == item)


def chest_fill(ch, slot, item, count):
    execs(f"item replace block {ch[0]} {ch[1]} {ch[2]} container.{slot} with {item} {count}")


def fresh_mac(pos):
    place(pos, "industrialreforged:macerator_4_level")
    wait(5)
    clear_container(pos, "inputSlot", 4)
    clear_container(pos, "outputSlot", 4)
    clear_container(pos, "upgradeSlot", 8)


def main():
    execs(AREA)
    execs("fill 244 208 -370 276 213 -360 minecraft:air")
    wait(10)

    # ---- 1. эжектор: готовое уезжает в сундук снизу ----------------------
    fresh_mac(MAC1)
    chest1 = (MAC1[0], MAC1[1] - 1, MAC1[2])
    place(chest1, "minecraft:chest")
    be_set(MAC1, setField="upgradeSlot", slot=0, item=EJECT, count=1)
    be_set(MAC1, setField="inputSlot", slot=0, item=ORE, count=64)
    energy_set(MAC1, 10_000_000)
    wait(420)
    got = chest_count(chest1, DUST)
    left_out = count_of(MAC1, "outputSlot", DUST)
    check("эжектор: продукт в сундуке", got >= 2, f"в сундуке {got}")
    check("эжектор: выход пуст", left_out == 0, f"в выходе {left_out}")

    # ---- 2. выход — только выход: что лежит, то и уезжает, без петель ----
    fresh_mac(MAC2)
    chest2 = (MAC2[0], MAC2[1] - 1, MAC2[2])
    place(chest2, "minecraft:chest")
    # положительный контроль чтения сундука (иначе нули неинформативны)
    chest_fill(chest2, 0, ORE, 7)
    wait(5)
    check("контроль: чтение сундука видит предметы", chest_count(chest2, ORE) == 7,
          f"прочитано {chest_count(chest2, ORE)}")
    execs(f"item replace block {chest2[0]} {chest2[1]} {chest2[2]} container.0 with minecraft:air")
    be_set(MAC2, setField="upgradeSlot", slot=0, item=EJECT, count=1)
    be_set(MAC2, setField="outputSlot", slot=0, item=ORE, count=10)
    be_set(MAC2, setField="outputSlot", slot=1, item=DUST, count=5)
    wait(30)
    ore_in_chest = chest_count(chest2, ORE)
    ore_back = count_of(MAC2, "inputSlot", ORE)
    dust_out = chest_count(chest2, DUST)
    check("выход вывозит как есть (сырьё в сундуке)", ore_in_chest == 10, f"руды в сундуке {ore_in_chest}")
    check("выход не перетекает во вход", ore_back == 0, f"руды во входе {ore_back}")
    check("продукт уехал в сундук", dust_out == 5, f"пыли в сундуке {dust_out}")

    # ---- 3. петли нет: продукт не сосётся обратно во вход ----------------
    dust_before = chest_count(chest2, DUST)
    be_set(MAC2, setField="inputSlot", slot=0, item=ORE, count=64)
    be_set(MAC2, setField="outputSlot", slot=0, item=DUST, count=10)
    energy_set(MAC2, 10_000_000)
    wait(420)
    dust_in_input = count_of(MAC2, "inputSlot", DUST)
    check("продукт не возвращается во вход", dust_in_input == 0, f"пыли во входе {dust_in_input}")
    check("продукт вывезен в сундук, а не съеден", chest_count(chest2, DUST) >= dust_before + 10,
          f"пыли в сундуке {chest_count(chest2, DUST)} (было {dust_before})")

    # ---- 4. скорость = числу в стаке --------------------------------------
    # Сундук копит пыль с пробы 1 (fresh_mac его не чистит) — считаем от базы.
    fresh_mac(MAC1)
    before1 = chest_count(chest1, DUST)
    be_set(MAC1, setField="upgradeSlot", slot=0, item=EJECT, count=4)
    be_set(MAC1, setField="outputSlot", slot=0, item=DUST, count=64)
    be_set(MAC1, setField="outputSlot", slot=1, item=DUST, count=64)
    wait(30)
    got1 = chest_count(chest1, DUST) - before1
    left1 = count_of(MAC1, "outputSlot", DUST)
    check("стак ×4 вывозит 128 за ~30 тиков", got1 == 128 and left1 == 0, f"в сундуке +{got1}, осталось {left1}")

    # ---- 5. веер: забитая первая сторона не морит остальных ----------------
    # Порядок null-стороны фиксирован (DOWN первый) by design: весь выход при
    # свободном низе уходит вниз. Анти-голод проверяем наоборот: низ забит —
    # вывоз обязан идти через север тем же бюджетом стороны.
    chest_north = (MAC1[0], MAC1[1], MAC1[2] - 1)
    place(chest_north, "minecraft:chest")
    for s in range(27):
        chest_fill(chest1, s, ORE, 64)
    be_set(MAC1, setField="outputSlot", slot=0, item=DUST, count=64)
    be_set(MAC1, setField="outputSlot", slot=1, item=DUST, count=64)
    wait(30)
    got_n = chest_count(chest_north, DUST)
    check("веер: север вывозит при забитом низе", got_n == 128, f"север {got_n}")
    check("веер: всё вывезено", count_of(MAC1, "outputSlot", DUST) == 0,
          f"осталось {count_of(MAC1, 'outputSlot', DUST)}")

    # ---- 6. затягиватель тянет из сундука сверху --------------------------
    fresh_mac(MAC3)
    chest3 = (MAC3[0], MAC3[1] + 1, MAC3[2])
    place(chest3, "minecraft:chest")
    chest_fill(chest3, 0, ORE, 32)
    be_set(MAC3, setField="upgradeSlot", slot=0, item=PULL, count=1)
    wait(30)
    pulled = count_of(MAC3, "inputSlot", ORE)
    left_ch = chest_count(chest3, ORE)
    check("затягиватель тянет руду во вход", pulled == 32 and left_ch == 0, f"во входе {pulled}, в сундуке {left_ch}")

    # ---- 7. верх insert-only: через верх не выгрести ----------------------
    fresh_mac(MAC3)
    be_set(MAC3, setField="outputSlot", slot=0, item=ORE, count=20)
    above = (MAC3[0], MAC3[1] + 1, MAC3[2])
    place(above, "industrialreforged:macerator_4_level")
    wait(5)
    clear_container(above, "inputSlot", 4)
    clear_container(above, "outputSlot", 4)
    clear_container(above, "upgradeSlot", 8)
    be_set(above, setField="upgradeSlot", slot=0, item=PULL, count=1)
    below = (MAC3[0], MAC3[1] - 1, MAC3[2])
    place(below, "industrialreforged:macerator_4_level")
    wait(5)
    clear_container(below, "inputSlot", 4)
    clear_container(below, "outputSlot", 4)
    clear_container(below, "upgradeSlot", 8)
    be_set(below, setField="upgradeSlot", slot=0, item=PULL, count=1)
    wait(40)
    top_got = count_of(above, "inputSlot", ORE)
    bottom_got = count_of(below, "inputSlot", ORE)
    check("верх: сосед сверху ничего не вытянул", top_got == 0, f"сверху утянуто {top_got}")
    check("низ: сосед снизу тянет через выход", bottom_got == 20, f"снизу утянуто {bottom_got}")

    # ---- 8. автобаланс: стак делится поровну по входам --------------------
    BAL = (MAC1[0] + 10, MAC1[1], MAC1[2])
    place(BAL, "industrialreforged:macerator_4_level")
    wait(5)
    clear_container(BAL, "inputSlot", 4)
    clear_container(BAL, "outputSlot", 4)
    clear_container(BAL, "upgradeSlot", 8)
    be_set(BAL, setField="inputSlot", slot=0, item=ORE, count=64)
    wait(60)
    spread = sorted((s.get("count", 0) for s in field_slots(BAL, "inputSlot")), reverse=True)
    check("автобаланс: 64 делятся 16/16/16/16", spread == [16, 16, 16, 16], f"слоты {spread}")

    # ---- 9. одиночка не размазывается --------------------------------------
    clear_container(BAL, "inputSlot", 4)
    be_set(BAL, setField="inputSlot", slot=0, item=ORE, count=1)
    wait(60)
    ones = sorted((s.get("count", 0) for s in field_slots(BAL, "inputSlot")), reverse=True)
    check("автобаланс: 1 предмет остаётся в одном слоте", ones == [1, 0, 0, 0], f"слоты {ones}")

    # ---- 10. с укреплением тоже ровно, без потерь --------------------------
    be_set(BAL, setField="upgradeSlot", slot=0, item="industrialreforged:strengthening_upgrade", count=1)
    clear_container(BAL, "inputSlot", 4)
    be_set(BAL, setField="inputSlot", slot=0, item=ORE, count=6)
    wait(60)
    six = sorted((s.get("count", 0) for s in field_slots(BAL, "inputSlot")), reverse=True)
    check("автобаланс: 6 делятся 2/2/1/1 без потерь", six == [2, 2, 1, 1], f"слоты {six}")

    # ---- 11. мусор без рецепта не растаскивается -----------------------------
    clear_container(BAL, "upgradeSlot", 8)
    clear_container(BAL, "inputSlot", 4)
    be_set(BAL, setField="inputSlot", slot=0, item="minecraft:dirt", count=10)
    wait(60)
    dirt = [s.get("count", 0) for s in field_slots(BAL, "inputSlot")]
    check("автобаланс: мусор без рецепта лежит где лежал", dirt == [10, 0, 0, 0], f"слоты {dirt}")

    # ---- 12. с забитым выходом не раздаём -------------------------------------
    for s in range(4):
        be_set(BAL, setField="outputSlot", slot=s, item="minecraft:dirt", count=64)
    clear_container(BAL, "inputSlot", 4)
    be_set(BAL, setField="inputSlot", slot=0, item=ORE, count=10)
    wait(60)
    blocked = [s.get("count", 0) for s in field_slots(BAL, "inputSlot")]
    check("автобаланс: при забитом выходе не раздаём", blocked == [10, 0, 0, 0], f"слоты {blocked}")

    return t.summary("tests_transfer")


if __name__ == "__main__":
    raise SystemExit(main())
