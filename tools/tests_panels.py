#!/usr/bin/env python3
"""E2E-тесты солнечных панелей: день/ночь/дождь, модули, отсутствие ночных панелей.

Ожидания считаются от `defaultProduction` панели (5 ур.), чтобы не зависеть от конфига.

Usage: python3 tools/tests_panels.py
"""

import math

import irbridge_testkit as t
from irbridge_testkit import check, skip, execs, wait, be, be_field, be_set, place, setblock

PANEL = (240, 181, -400)
MODULES = ["solar_module_rain_lens_1", "solar_module_rain_lens_2", "solar_module_day_1", "solar_module_day_2",
           "solar_module_sun_lens_1", "solar_module_sun_lens_2", "solar_module_night_1", "solar_module_moon_lens_1",
           "solar_module_storage_1", "solar_module_output_1"]


def rnd(value):
    """Java Math.round(double)."""
    return int(math.floor(value + 0.5))


def clear_modules():
    for slot in range(4):
        be_set(PANEL, setField="moduleSlot", slot=slot, item="minecraft:air", count=0)


def production():
    return int(be_field(PANEL, "production"))


def main():
    execs("forceload add 230 -420 320 -380")
    # 26-й мир может быть в сухом биоме — дождевые тесты требуют осадков
    execs("fillbiome 235 180 -405 245 185 -395 minecraft:plains")
    execs("fill 230 179 -420 320 -380 minecraft:iron_block")
    execs("fill 230 180 -420 320 -380 minecraft:air")
    wait(20)
    place(PANEL, "industrialreforged:solar_generator_5_level")
    wait(140)
    if be_field(PANEL, "wetBiome") != "true":
        skip("дождевые тесты", "биом без осадков")

    base = int(be_field(PANEL, "defaultProduction"))

    # ---- день / ночь -----------------------------------------------------
    execs("time set day", "weather clear")
    clear_modules()
    wait(160)
    check(f"день: панель даёт 100% ({base})", production() == base, production())
    execs("time set midnight")
    wait(40)
    night = rnd(base * 0.6)
    check(f"ночь: панель даёт 60% ({night})", production() == night, production())

    # ---- дождь: базовые 40% + линза сверху -------------------------------
    execs("weather rain")
    wait(160)
    rain = rnd(base * 0.4)
    check(f"дождь без линзы: базовые 40% ({rain})", production() == rain, production())
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_rain_lens_1")
    wait(60)
    rain1 = rnd(base * 0.44)
    check(f"линза дождя I: 40 x 1.1 = 44% ({rain1})", production() == rain1, production())
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_rain_lens_2")
    wait(60)
    rain2 = rnd(base * 0.5)
    check(f"линза дождя II: 40 x 1.25 = 50% ({rain2})", production() == rain2, production())

    # ---- модули дня ------------------------------------------------------
    execs("weather clear", "time set day")
    clear_modules()
    wait(200)
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_day_1")
    wait(40)
    day1 = rnd(base * 1.1)
    check(f"модуль дня I: +10% ({day1})", production() == day1, production())
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_day_2")
    wait(40)
    day2 = rnd(base * 1.25)
    check(f"модуль дня II: +25% ({day2})", production() == day2, production())
    be_set(PANEL, setField="moduleSlot", slot=1, item="industrialreforged:solar_module_sun_lens_2")
    wait(40)
    sun = rnd(base * 1.25 * 1.25)
    check(f"линза солнца II множит поверх модуля ({sun})", production() == sun, production())

    # ---- модули ночи -----------------------------------------------------
    execs("time set midnight")
    clear_modules()
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_night_1")
    wait(40)
    night1 = rnd(base * 0.6 * 1.1)
    check(f"модуль ночи I: 60% x 1.1 ({night1})", production() == night1, production())
    be_set(PANEL, setField="moduleSlot", slot=1, item="industrialreforged:solar_module_moon_lens_2")
    wait(40)
    moon = rnd(base * 0.6 * 1.1 * 1.25)
    check(f"линза луны II множит ночь ({moon})", production() == moon, production())

    # ---- хранение и выход -------------------------------------------------
    execs("time set day")
    clear_modules()
    base_capacity = max(base * 400, 4000)
    capacity_before = int(be(PANEL)["fields"]["energy"]["capacity"])
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_storage_1")
    wait(40)
    capacity_after = int(be(PANEL)["fields"]["energy"]["capacity"])
    check(f"модуль хранения I: буфер {base_capacity} -> {rnd(base_capacity * 1.1)}",
          capacity_before == base_capacity and capacity_after == rnd(base_capacity * 1.1),
          f"{capacity_before} -> {capacity_after}")
    base_extract = max(1, rnd(base * 4))
    extract_before = int(be(PANEL)["fields"]["energy"]["extract"])
    be_set(PANEL, setField="moduleSlot", slot=0, item="industrialreforged:solar_module_output_1")
    wait(40)
    extract_after = int(be(PANEL)["fields"]["energy"]["extract"])
    check(f"модуль выхода I: отдача {base_extract} -> {rnd(base * 4 * 1.1)}",
          extract_before == base_extract and extract_after == max(1, rnd(base * 4 * 1.1)),
          f"{extract_before} -> {extract_after}")

    # ---- ночные панели удалены -------------------------------------------
    result = execs(f"setblock 245 181 -400 industrialreforged:night_solar_generator_1_level")
    output = " ".join(result.get("output", [])).lower()
    check("ночные панели убраны из реестра", "unknown" in output or "неизвестн" in output, output[:80])

    return t.summary("tests_panels")


if __name__ == "__main__":
    raise SystemExit(main())
