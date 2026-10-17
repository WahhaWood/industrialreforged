#!/usr/bin/env python3
"""E2E 1.1.0: утилизатор 1–4, генератор материи со скидкой утильсырья, ME-машины и ME-пожиратель.

ME-часть нужна dev-серверу с AE2 (localRuntime в build.gradle); без AE2 она пропускается.
Сеть собирается вокруг контроллера: каждое устройство прилегает к своей грани, так что
каналы приходят напрямую без кабелей.

Usage: IRBRIDGE_PORT=9879 python3 tools/tests_me.py
"""

import irbridge_testkit as t
from irbridge_testkit import call, check, execs, wait, be_set, energy_set, place, clear_container, skip

AREA = "forceload add 600 -600 660 -560"
Y = 200

# --- утилизатор и генератор материи (без AE2) ---
REC4 = (606, Y, -590)
MATTER = (612, Y, -590)

# --- ME-сеть ---
CONTROLLER = (630, Y, -580)
CELL = (629, Y, -580)          # -x: бесконечная энергия (часть проверок — без неё)
DRIVE = (631, Y, -580)         # +x: привод с ячейкой
CPU = (630, Y + 1, -580)       # +y: 1k крафтового хранилища ...
COPROC = (630, Y + 2, -580)    # ... и ускоритель над ним (CPU 1×2×1)
MAC = (630, Y, -579)           # +z: ME-дробитель
FURNACE = (630, Y, -581)       # -z: ME-печь
EATER = (630, Y - 1, -580)     # -y: ME-пожиратель


def slots(pos, field):
    entry = t.be(pos).get("fields", {}).get(field, {})
    return entry.get("slots", []) if isinstance(entry, dict) else []


def count_of(pos, field, item):
    return sum(int(s.get("count", 0)) for s in slots(pos, field) if s.get("item") == item)


def ae2(op, pos, **params):
    payload = {"op": op, "x": pos[0], "y": pos[1], "z": pos[2]}
    payload.update(params)
    return call("/ae2", payload)


def stored(item):
    return int(ae2("count", CONTROLLER, item=item).get("count", -1))


# ----------------------------------------------------------------------

def test_recycler():
    place(REC4, "industrialreforged:recycler_4_level")
    wait(5)
    for field, size in (("inputSlot", 4), ("outputSlot", 4), ("upgradeSlot", 8)):
        clear_container(REC4, field, size)
    # 7 ускорителей 3 ур. сжимают 45 тиков до 1, укрепление даёт 4 предмета за операцию
    be_set(REC4, setField="upgradeSlot", slot=0, item="industrialreforged:overclocker_3_level_upgrade", count=7)
    be_set(REC4, setField="upgradeSlot", slot=1, item="industrialreforged:strengthening_upgrade", count=1)
    for slot in range(4):
        be_set(REC4, setField="inputSlot", slot=slot, item="minecraft:cobblestone", count=64)
    energy_set(REC4, 2_000_000_000)
    wait(200)
    left = count_of(REC4, "inputSlot", "minecraft:cobblestone")
    scrap = count_of(REC4, "outputSlot", "industrialreforged:scrap")
    check("утилизатор 4: 256 булыжника переработано", left == 0, f"осталось {left}")
    # 256 бросков по 1/8: ожидание 32, σ ≈ 5.3 — коридор ±4σ
    check("утилизатор 4: утильсырьё ≈ 1/8", 11 <= scrap <= 53, f"утильсырья {scrap} из 256")

    # чёрный список: палки в слоте не обрабатываются
    clear_container(REC4, "inputSlot", 4)
    be_set(REC4, setField="inputSlot", slot=0, item="minecraft:stick", count=16)
    wait(60)
    sticks = count_of(REC4, "inputSlot", "minecraft:stick")
    check("утилизатор: палки (чёрный список) не берёт в работу", sticks == 16, f"палок {sticks}")


def test_matter_generator():
    place(MATTER, "industrialreforged:matter_generator")
    wait(5)
    energy_set(MATTER, 3_999_999)
    wait(10)
    uu = mb(MATTER)
    check("генератор материи: 3 999 999 FE мало на 1 мБ", uu == 0, f"UU {uu}")
    energy_set(MATTER, 4_000_000)
    wait(10)
    uu = mb(MATTER)
    check("генератор материи: 4M FE = 1 мБ", uu == 1, f"UU {uu}")

    # 32 утильсырья = полная скидка 1M: мБ стоит 3M
    be_set(MATTER, setField="amplifierSlot", slot=0, item="industrialreforged:scrap", count=32)
    energy_set(MATTER, 3_000_000)
    wait(10)
    uu = mb(MATTER)
    amp = int(t.be_field(MATTER, "amplifier"))
    check("генератор материи: 32 утильсырья + 3M FE = 1 мБ", uu == 2, f"UU {uu}")
    check("генератор материи: скидка 1M израсходована целиком", amp == 0, f"запас {amp}")

    # коробка: 281 250 FE скидки
    be_set(MATTER, setField="amplifierSlot", slot=0, item="industrialreforged:scrap_box", count=1)
    energy_set(MATTER, 4_000_000 - 281_250)
    wait(10)
    uu = mb(MATTER)
    check("генератор материи: коробка = скидка 281 250 FE", uu == 3, f"UU {uu}")

    # запас набирается только до потолка цикла (1M на 1 мБ), остальное ждёт в слоте
    energy_set(MATTER, 0)
    be_set(MATTER, setField="amplifierSlot", slot=0, item="industrialreforged:scrap", count=40)
    wait(5)
    amp = int(t.be_field(MATTER, "amplifier"))
    left = count_of(MATTER, "amplifierSlot", "industrialreforged:scrap")
    check("генератор материи: в запас взято 32 утильсырья (1M)", amp == 1_000_000 and left == 8, f"запас {amp}, в слоте {left}")


def mb(pos):
    tanks = t.fluids(pos).get("tanks", [])
    return int(tanks[0].get("amount", 0)) if tanks else 0


# ----------------------------------------------------------------------

def build_network(with_power=True):
    for pos in (CONTROLLER, CELL, DRIVE, CPU, COPROC, MAC, FURNACE, EATER):
        execs(f"setblock {pos[0]} {pos[1]} {pos[2]} minecraft:air")
    wait(2)
    execs(f"setblock {CONTROLLER[0]} {CONTROLLER[1]} {CONTROLLER[2]} ae2:controller")
    if with_power:
        execs(f"setblock {CELL[0]} {CELL[1]} {CELL[2]} ae2:creative_energy_cell")
    execs(f"setblock {DRIVE[0]} {DRIVE[1]} {DRIVE[2]} ae2:drive",
          f"setblock {CPU[0]} {CPU[1]} {CPU[2]} ae2:1k_crafting_storage",
          f"setblock {COPROC[0]} {COPROC[1]} {COPROC[2]} ae2:crafting_accelerator",
          f"setblock {MAC[0]} {MAC[1]} {MAC[2]} industrialreforged:me_macerator",
          f"setblock {FURNACE[0]} {FURNACE[1]} {FURNACE[2]} industrialreforged:me_furnace",
          f"setblock {EATER[0]} {EATER[1]} {EATER[2]} industrialreforged:me_recycler")
    wait(20)
    put = ae2("put", DRIVE, item="ae2:item_storage_cell_64k")
    check("сеть: ячейка 64k в приводе", put.get("inserted") is True, str(put))
    wait(40)


def test_me():
    status = call("/ae2", {"op": "info", "x": 0, "y": 0, "z": 0})
    if "not active" in str(status.get("error", "")):
        skip("ME-машины", "интеграция AE2 не активна (сервер без AE2)")
        return

    build_network()
    info = ae2("info", MAC)
    check("ME-дробитель: в сети и активен", info.get("active") is True, str(info))
    check("ME-дробитель: все рецепты выставлены паттернами",
          info.get("patterns", 0) > 0 and info.get("patterns") == info.get("recipes"), str(info))
    finfo = ae2("info", FURNACE)
    check("ME-печь: по умолчанию все рецепты выключены",
          finfo.get("patterns") == 0 and finfo.get("recipes", 0) > 0, str(finfo))
    check("сеть: крафтовый процессор собран", ae2("info", CONTROLLER).get("cpus") == 1, str(ae2("info", CONTROLLER)))
    check("сеть: дроблёное железо можно заказать",
          ae2("craftable", CONTROLLER, item="industrialreforged:crushed_iron").get("craftable") is True)

    # заказ через крафтовый процессор: вход по тегу c:ores/iron, в сети только глубинная руда
    ae2("insert", CONTROLLER, item="minecraft:deepslate_iron_ore", count=64)
    craft = ae2("craft", CONTROLLER, item="industrialreforged:crushed_iron", count=32)
    check("заказ 32 дроблёного железа принят", craft.get("ok") is True, str(craft))
    wait(120)
    crushed = stored("industrialreforged:crushed_iron")
    ore = stored("minecraft:deepslate_iron_ore")
    check("заказ выполнен: 32 дроблёного железа в сети", crushed == 32, f"в сети {crushed}")
    check("на заказ ушло 16 глубинной руды (замена по тегу)", ore == 48, f"руды {ore}")

    # 1024 операции за тик в среднем: проход раз в 5 тиков делает работу этих 5 тиков, очередь — на два прохода
    before = stored("industrialreforged:crushed_iron")
    pushed = ae2("push", MAC, recipe="industrialreforged:macerator/iron_ore", count=12000).get("pushed")
    check("ME-дробитель: очередь принимает 10 240 операций", pushed == 10240, f"принято {pushed}")
    wait(2)
    first = ae2("info", MAC)
    check("ME-дробитель: за проход не больше 5×1024, в среднем не больше 1024 за тик",
          first.get("queued", 0) >= 10240 - 5 * 1024 and 0 < first.get("lastOperations", 0) <= 1024, str(first))
    wait(15)
    after = stored("industrialreforged:crushed_iron")
    check("ME-дробитель: 10 240 операций за ~10 тиков", after - before == 20480 and ae2("info", MAC).get("queued") == 0,
          f"прибавилось {after - before}")

    # переключатели
    ae2("toggle", MAC, action="DISABLE_ALL")
    off = ae2("info", MAC)
    check("«выкл все»: паттерны убраны из сети", off.get("patterns") == 0, str(off))
    wait(5)
    check("«выкл все»: дроблёное железо больше не заказывается",
          ae2("craftable", CONTROLLER, item="industrialreforged:crushed_iron").get("craftable") is False)
    ae2("toggle", MAC, action="ENABLE_ALL")
    ae2("toggle", MAC, action="TOGGLE", recipe="industrialreforged:macerator/iron_ore")
    one = ae2("info", MAC)
    check("один рецепт выключен: паттернов на 1 меньше", one.get("patterns") == one.get("recipes", 0) - 1, str(one))
    ae2("toggle", MAC, action="TOGGLE", recipe="industrialreforged:macerator/iron_ore")
    ae2("toggle", FURNACE, action="ENABLE_ALL")
    wait(5)
    check("ME-печь «вкл все»: железный слиток заказывается",
          ae2("craftable", CONTROLLER, item="minecraft:iron_ingot").get("craftable") is True)

    # /reload: рецепты перечитаны, машины заново выставили паттерны, переключатели целы
    execs("reload")
    wait(40)
    again = ae2("info", MAC)
    check("/reload: паттерны ME-дробителя выставлены заново",
          again.get("patterns", 0) > 0 and again.get("patterns") == again.get("recipes"), str(again))
    check("/reload: дроблёное железо снова заказывается",
          ae2("craftable", CONTROLLER, item="industrialreforged:crushed_iron").get("craftable") is True)
    check("/reload: ME-печь осталась включённой", ae2("info", FURNACE).get("patterns", 0) > 0)

    # ME-пожиратель: 800 булыжника -> 0 в сети, ровно 100 утильсырья
    ae2("insert", CONTROLLER, item="minecraft:cobblestone", count=800)
    ae2("insert", CONTROLLER, item="minecraft:stick", count=16)
    ae2("sample", EATER, slot=0, item="minecraft:cobblestone")
    ae2("sample", EATER, slot=1, item="minecraft:stick")
    wait(30)
    cobble = stored("minecraft:cobblestone")
    scrap = stored("industrialreforged:scrap")
    sticks = stored("minecraft:stick")
    eater = ae2("info", EATER)
    check("ME-пожиратель: булыжник съеден до нуля", cobble == 0, f"осталось {cobble}")
    check("ME-пожиратель: 800 предметов = ровно 100 утильсырья", scrap == 100, f"утильсырья {scrap}")
    check("ME-пожиратель: палки (чёрный список) не тронуты", sticks == 16, f"палок {sticks}")
    check("ME-пожиратель: счётчик 800", eater.get("recycled") == 800, str(eater))


def test_me_energy():
    """Без бесконечной энергии: операция стоит FE машины / 2 AE, буфер машины — хранилище сети."""
    status = call("/ae2", {"op": "info", "x": 0, "y": 0, "z": 0})
    if "not active" in str(status.get("error", "")):
        return

    build_network(with_power=False)
    ae2("charge", MAC, amount=1_000_000)
    wait(45)  # AE2 включает питание сети после 30 тиков устойчивой энергии
    before = ae2("info", MAC).get("buffer", 0)
    pushed = ae2("push", MAC, recipe="industrialreforged:macerator/iron_ore", count=1000).get("pushed")
    check("сеть на буфере ME-машины: 1000 операций принято", pushed == 1000, f"принято {pushed}")
    wait(5)
    after = ae2("info", MAC).get("buffer", 0)
    spent = before - after
    # 1000 операций по 450 AE (3 FE/т × 300 т / 2) = 450 000; сверху — простой сети и запись в ячейку
    check("ME-дробитель: 1000 операций ≈ 450 000 AE", 450_000 <= spent <= 470_000, f"потрачено {spent:.0f} AE")
    info = ae2("info", MAC)
    check("буфер ME-машины питает сеть", info.get("powered") is True and info.get("queued") == 0, str(info))

    # энергии на всё не хватает: остаток очереди ждёт
    ae2("charge", MAC, amount=100_000)
    wait(5)
    ae2("push", MAC, recipe="industrialreforged:macerator/iron_ore", count=1000)
    wait(10)
    # 100 000 AE хватает примерно на 220 операций по 450 AE
    left = ae2("info", MAC).get("queued", 0)
    check("нет энергии — операции ждут в очереди, не теряются", 700 <= left < 1000, f"в очереди {left}")


def main():
    execs(AREA)
    execs(f"fill 600 {Y - 2} -600 660 {Y + 3} -560 minecraft:air")
    wait(10)
    test_recycler()
    test_matter_generator()
    test_me()
    test_me_energy()
    return t.summary("tests_me")


if __name__ == "__main__":
    raise SystemExit(main())
