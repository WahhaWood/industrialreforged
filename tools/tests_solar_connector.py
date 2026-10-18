#!/usr/bin/env python3
"""E2E: модули (линзы и остальные) действуют на соединитель панелей целиком.

Соединитель с панелями 5 ур. (3 шт.), потом добавляются модули в его четыре слота модулей
(NBT `solar_modules`): хранение, отдача, линза солнца/луны/дождя, модули дня/ночи.
Сравнивается выработка (`production`), ёмкость и отдача (`/energy`) с голым соединителем.

Usage: IRBRIDGE_PORT=9879 python3 tools/tests_solar_connector.py
"""

import irbridge_testkit as t
from irbridge_testkit import call, check, execs, wait

X, Y, Z = 300, 200, -300
PANEL = "industrialreforged:solar_generator_5_level"
MOD = "industrialreforged:solar_module_"


def field(name):
    entry = call(f"/be?x={X}&y={Y}&z={Z}").get("fields", {}).get(name, {})
    return entry.get("value") if isinstance(entry, dict) else entry


def modules(*ids):
    items = ",".join(f'{{Slot:{i}b,id:"{MOD}{name}",count:1}}' for i, name in enumerate(ids))
    execs(f"data merge block {X} {Y} {Z} {{solar_modules:[{items}]}}")
    wait(40)


def state():
    e = call(f"/energy?x={X}&y={Y}&z={Z}")
    return {"production": int(field("production")), "capacity": e["capacityLong"], "extract": e["extract"]}


def main():
    execs(f"forceload add {X - 8} {Z - 8} {X + 8} {Z + 8}", "weather clear", "time set noon",
          f"setblock {X} {Y} {Z} minecraft:air", f"setblock {X} {Y} {Z} industrialreforged:solar_connector")
    wait(40)
    execs(f'data merge block {X} {Y} {Z} {{inventory:[{{Slot:0b,id:"{PANEL}",count:3}}]}}')
    wait(150)  # the sun check and the panels are refreshed on a timer, not by /data merge
    check("соединитель видит небо", field("sunIsVisible") == "true" and field("sunIsUp") == "true", "нужен открытый верх")
    base = state()
    check("голый соединитель с панелями: выработка, ёмкость и отдача есть", all(v > 0 for v in base.values()), str(base))
    check("без модулей отдача вдвое больше дневной выработки", base["extract"] == 2 * base["production"], str(base))

    modules("sun_lens_2")
    lens = state()
    check("линза солнца II (+25%) умножает дневную выработку всего соединителя",
          lens["production"] == round(base["production"] * 1.25), f"{base['production']} -> {lens['production']}")
    check("линза не меняет ёмкость и отдачу", lens["capacity"] == base["capacity"] and lens["extract"] == base["extract"], str(lens))

    modules("storage_2", "output_2")
    both = state()
    check("модуль хранения II (+25%) — ёмкость соединителя", both["capacity"] == round(base["capacity"] * 1.25), f"{base['capacity']} -> {both['capacity']}")
    check("модуль отдачи II (+25%) — отдача соединителя", both["extract"] == round(base["extract"] * 1.25), f"{base['extract']} -> {both['extract']}")
    check("хранение и отдача не меняют выработку", both["production"] == base["production"], str(both))

    modules("day_2", "sun_lens_2")
    day = state()
    check("модуль дня II (+25%) и линза солнца II (+25%): ×1,25 × 1,25",
          day["production"] == round(base["production"] * 1.25 * 1.25), f"{base['production']} -> {day['production']}")

    modules("rain_lens_2")
    check("линза дождя II: доля дождя 0,4 × 1,25", abs(float(field("rainRate")) - 0.5) < 1e-9, str(field("rainRate")))

    execs("time set midnight")
    wait(140)
    modules()
    night = state()
    check("ночью без модулей — 60% дневной", night["production"] == round(base["production"] * 0.6), f"{base['production']} -> {night['production']}")
    modules("moon_lens_2")
    moon = state()
    check("линза луны II (+25%) умножает ночную выработку",
          abs(moon["production"] - base["production"] * 0.6 * 1.25) <= 0.5, f"{night['production']} -> {moon['production']}")
    modules("sun_lens_2")
    check("линза солнца ночью не работает", state()["production"] == night["production"], str(state()))

    execs(f"setblock {X} {Y} {Z} minecraft:air", f"forceload remove {X - 8} {Z - 8} {X + 8} {Z + 8}", "time set noon")
    return t.summary("tests_solar_connector")


if __name__ == "__main__":
    raise SystemExit(main())
