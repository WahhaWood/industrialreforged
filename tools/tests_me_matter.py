#!/usr/bin/env python3
"""E2E ME-генератор материи и FE-вход ME-узлов.

Сеть питается только обычной ячейкой энергии (без креативной), поэтому энергия считается точно:
ячейка — «сеть», собственный буфер генератора или машины — то, что принёс провод (FE → AE, 2:1).

Usage: IRBRIDGE_PORT=9879 python3 tools/tests_me_matter.py
"""

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait
from bench_me import ae2

Y = 200
AREA = "forceload add 750 -590 770 -570"
CTRL = (760, Y, -580)
POWER = (759, Y, -580)   # ячейка энергии: питает сеть
DRIVE = (761, Y, -580)
GEN = (760, Y, -579)
MAC2 = (760, Y, -581)    # второй ME-дробитель: платит за операции из своего буфера

SCRAP = "industrialreforged:scrap"
BOX = "industrialreforged:scrap_box"


def count(item):
    return int(ae2("count", CTRL, item=item).get("count", 0))


def grid_power():
    return float(ae2("info", CTRL).get("gridStoredPower", 0))


def uu():
    return int(ae2("uu", CTRL).get("uu", 0))


def main():
    execs(AREA, f"fill 755 {Y - 2} -585 766 {Y + 3} -575 minecraft:air")
    wait(5)
    execs(f"setblock {CTRL[0]} {CTRL[1]} {CTRL[2]} ae2:controller",
          f"setblock {POWER[0]} {POWER[1]} {POWER[2]} ae2:energy_cell",
          f"setblock {DRIVE[0]} {DRIVE[1]} {DRIVE[2]} ae2:drive",
          f"setblock {GEN[0]} {GEN[1]} {GEN[2]} industrialreforged:me_matter_generator",
          f"setblock {MAC2[0]} {MAC2[1]} {MAC2[2]} industrialreforged:me_macerator")
    wait(20)
    ae2("put", DRIVE, item="ae2:item_storage_cell_256k")
    ae2("charge", POWER, amount=100_000)  # сеть живая и почти без запаса: 200k FE на всё
    ae2("charge", GEN, amount=0)
    wait(40)
    info = ae2("info", GEN)
    check("ME-генератор в сети, питание есть", info.get("active") is True and info.get("powered") is True, str(info))

    # --- FE-вход всех узлов ---
    start = ae2("info", MAC2).get("buffer", 0)
    fe = ae2("fe", MAC2, amount=1000)
    check("ME-дробитель принимает FE от кабеля (2 FE = 1 AE)", fe.get("accepted") == 1000, str(fe))
    gained = ae2("info", MAC2).get("buffer", 0) - start
    check("FE ушёл в буфер ME-дробителя: ровно +500 AE", gained == 500, f"+{gained}")
    ae2("charge", POWER, amount=100_000)

    # --- кабель видит узлы как обычные потребители (capability EnergyStorage на всех сторонах) ---
    for name, pos in (("ME-генератор материи", GEN), ("ME-дробитель", MAC2)):
        cap = t.call(f"/energy?x={pos[0]}&y={pos[1]}&z={pos[2]}&side=up")
        check(f"{name}: на стороне кабеля есть энергетическая capability, только приём",
              cap.get("found") is True and cap.get("canReceive") is True and cap.get("canExtract") is False, str(cap))

    # --- ME-машина платит за операции сначала из буфера, что принёс кабель ---
    ae2("charge", MAC2, amount=0)
    ae2("fe", MAC2, amount=10_000)  # 5000 AE
    net_before = grid_power()
    own_before = ae2("info", MAC2).get("buffer", 0)
    pushed = ae2("push", MAC2, recipe="industrialreforged:macerator/iron_ore", count=4).get("pushed")
    wait(20)
    own_spent = own_before - ae2("info", MAC2).get("buffer", 0)
    net_spent = net_before - grid_power()
    check("4 операции ME-дробителя: 4 × 450 AE из своего буфера", pushed == 4 and 1700 < own_spent < 1900, f"принято {pushed}, потрачено {own_spent}")
    check("сеть за это время почти ничего не отдала (только фоновый расход)", net_spent < 300, f"сеть отдала {net_spent}")
    ae2("charge", POWER, amount=100_000)

    # --- нет места для жидкости: ничего не делает, энергию не тратит ---
    fed = ae2("fe", GEN, amount=12_000_000)
    check("генератор принимает FE без предела: 12M за раз", fed.get("accepted") == 12_000_000, str(fed))
    wait(60)
    info = ae2("info", GEN)
    check("в сети нет жидкостного хранилища: материи нет, энергия цела",
          uu() == 0 and info.get("produced") == 0 and abs(info.get("buffer", 0) - 6_000_000) < 1, str(info))

    # --- есть место: энергия кабеля идёт первой, сеть не тронута ---
    ae2("put", DRIVE, item="ae2:fluid_storage_cell_64k")
    wait(80)
    info = ae2("info", GEN)
    check("12M FE на буфере = 3 мБ (4M FE за мБ), без утильсырья", uu() == 3 and info.get("produced") == 3, f"UU {uu()}, {info}")
    check("буфер генератора потрачен: 3 × 2M AE", info.get("buffer", -1) < 100, str(info))
    power = grid_power()
    check("сеть (ячейка) почти не тронута: платил кабельный буфер", power > 90_000, f"в ячейке {power}")

    # --- утильсырьё и коробки из ME: скидка до 1M FE на мБ ---
    ae2("insert", CTRL, item=SCRAP, count=100)
    ae2("insert", CTRL, item=BOX, count=5)
    before = uu()
    ae2("fe", GEN, amount=6_000_000)
    wait(80)
    info = ae2("info", GEN)
    check("6M FE + скидка 1M на мБ = 2 мБ (цена 3M)", uu() - before == 2 and info.get("produced") == 5, f"UU +{uu() - before}, {info}")
    check("коробки взяты первыми: 5 коробок и 19 утильсырья на скидку 2M", count(BOX) == 0 and count(SCRAP) == 81, f"коробок {count(BOX)}, утильсырья {count(SCRAP)}")
    check("скидка записана: 2M FE", info.get("saved") == 2_000_000, str(info))

    # --- без утильсырья в сети цена полная ---
    before = uu()
    ae2("extract", CTRL, item=SCRAP, count=81)
    ae2("fe", GEN, amount=8_000_000)
    wait(80)
    check("утильсырья в сети нет: 8M FE = 2 мБ по полной цене", uu() - before == 2, f"UU +{uu() - before}")

    # --- мало утильсырья: берётся сколько есть, скидка соразмерно ---
    before = uu()
    ae2("insert", CTRL, item=SCRAP, count=8)  # 250 000 FE скидки
    ae2("fe", GEN, amount=7_750_000)          # 2 мБ стоят 8M − 250k
    wait(80)
    after = ae2("info", GEN)
    check("8 утильсырья = скидка 250k FE: 7,75M FE хватило на 2 мБ", uu() - before == 2 and count(SCRAP) == 0, f"UU +{uu() - before}, утильсырья {count(SCRAP)}, {after}")

    execs(f"fill 755 {Y - 2} -585 766 {Y + 3} -575 minecraft:air", AREA.replace(" add ", " remove "))
    return t.summary("tests_me_matter")


if __name__ == "__main__":
    raise SystemExit(main())
