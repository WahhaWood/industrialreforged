#!/usr/bin/env python3
"""Balances the solar panel chain: a copy of a panel should cost about 14-18
REAL hours of that panel's own output.

A real hour is 3600 s * 20 ticks, and the panel only generates 100% by day and
60% by night (average 80%), so the target cost is hours * 57600 * FE/t.

Writes the panel recipes (1 previous panel + components + a level core) and the
molecular transformer recipes for the photocells, then iterates the photocell
energies until the recursive calculator matches the target cost.
"""

import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import economy

RECIPES = os.path.join(economy.RECIPES)
# target time per copy ramps from 14 real h (panel 2) to 18 real h (panel 15),
# with a slight curve so the endgame panels are noticeably harder than the
# middle ones; panel 1 keeps its structural parts price
HOURS_FROM, HOURS_TO = 14.0, 18.0
TICKS_PER_HOUR = 3600 * 20  # 20 ticks per second
DAY_NIGHT_FACTOR = 0.8      # 100% day / 60% night on average


def hours_for(level):
    return HOURS_FROM + (HOURS_TO - HOURS_FROM) * ((level - 1) / 14.0) ** 1.5
# выработка панелей: дефолты IRConfig ×2 (было в 20 раз меньше оригинала, затем удвоено)
OUTPUT = {
    1: 4, 2: 20, 3: 100, 4: 500, 5: 2_500, 6: 12_500, 7: 62_500, 8: 312_500, 9: 1_562_500, 10: 7_000_000, 11: 30_000_000, 12: 130_000_000, 13: 550_000_000, 14: 2_350_000_000, 15: 10_000_000_000,
}
CIRCUITS = {2: "advanced_circuit", 3: "nano_circuit", 4: "quantum_circuit", 5: "singular_circuit"}
MODULES = {
    2: "quantum_module", 3: "radiation_quantum_module", 4: "unstable_quantum_module",
    5: "semi_stable_quantum_module", 6: "stable_quantum_module",
    7: "quantum_core_1_level", 8: "quantum_core_2_level", 9: "quantum_core_3_level",
    10: "quantum_core_1_level", 11: "quantum_core_2_level", 12: "quantum_core_3_level",
    13: "stable_quantum_module", 14: "quantum_core_3_level", 15: "stable_quantum_module",
}
target = {
    level: int(hours_for(level) * TICKS_PER_HOUR * DAY_NIGHT_FACTOR * output)
    for level, output in OUTPUT.items()
}
photocell_energy = {level: max(1, target[level] - target[level - 1]) for level in range(2, 16)}


def write_panel(level):
    if level == 1:
        key = {
            "P": {"item": "industrialreforged:photocell_1_level"},
            "C": {"item": "industrialreforged:circuit"},
            "G": {"item": "industrialreforged:generator"},
            "T": {"item": "industrialreforged:solar_core_1_level"},
        }
        recipe = {
            "type": "minecraft:crafting_shaped",
            "category": "misc",
            "key": key,
            "pattern": ["P P", "CGC", " T "],
            "result": {"count": 1, "id": "industrialreforged:solar_generator_1_level"},
        }
        json.dump(recipe, open(os.path.join(RECIPES, "shaped", "solar_generator_1_level.json"), "w", encoding="utf-8"), indent=2)
        return

    # раскладка: 1,3 — фотоэлементы; 2 — квантовое ядро/модуль тира;
    # 4,6 — одинаковые укреплённые иридиевые пластины; 5,7,9 — панели; 8 — ядро панели
    key = {
        "P": {"item": f"industrialreforged:solar_generator_{level - 1}_level"},
        "F": {"item": f"industrialreforged:photocell_{level}_level"},
        "Q": {"item": f"industrialreforged:{MODULES[level]}"},
        "I": {"item": "industrialreforged:iridium"},
        "T": {"item": f"industrialreforged:solar_core_{level}_level"},
    }
    recipe = {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": key,
        "pattern": ["FQF", "IPI", "PTP"],
        "result": {"count": 1, "id": f"industrialreforged:solar_generator_{level}_level"},
    }
    json.dump(recipe, open(os.path.join(RECIPES, "shaped", f"solar_generator_{level}_level.json"), "w", encoding="utf-8"), indent=2)


def write_photocell(level):
    """Фотоэлементы: крафт из предыдущего фотоэлемента + материалы тира
    (иридий, сплав, схема, укреплённое стекло). MT-варианты удаляются."""
    mt_path = os.path.join(RECIPES, "molecular_transformer", f"photocell_{level}_level.json")
    if os.path.exists(mt_path):
        os.remove(mt_path)

    # сплав по тиру: младшие уровни самые копируемые — им дешёвая суннариум-часть,
    # средние — суннариум-сплав, старшие — обогащённый
    if level <= 5:
        alloy = "sunnarium_part"
    elif level <= 10:
        alloy = "sunnarium_alloy"
    else:
        alloy = "enriched_sunnarium_alloy"
    recipe = {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
            "P": {"item": f"industrialreforged:photocell_{level - 1}_level"},
            "A": {"item": f"industrialreforged:{alloy}"},
            "C": {"item": f"industrialreforged:{CIRCUITS.get(level, 'singular_circuit')}"},
            "G": {"item": "industrialreforged:reinforced_glass"},
        },
        "pattern": ["GCG", "APA", "GCG"],
        "result": {"count": 1, "id": f"industrialreforged:photocell_{level}_level"},
    }
    json.dump(recipe, open(os.path.join(RECIPES, "shaped", f"photocell_{level}_level.json"), "w", encoding="utf-8"), indent=2)


def write_core(level):
    """Ядра панелей: ядро 1 — крафт, далее каждое ядро заряжается в молекулярном
    трансформере из предыдущего (плата 8M FE с удвоением за уровень)."""
    for folder in ("molecular_transformer", "compressor"):
        stale = os.path.join(RECIPES, folder, f"solar_core_{level}_level.json")
        if os.path.exists(stale):
            os.remove(stale)

    if level == 1:
        recipe = {
            "type": "minecraft:crafting_shaped",
            "category": "misc",
            "key": {
                "R": {"item": "minecraft:redstone"},
                "Q": {"item": "industrialreforged:quantum_module"},
                "I": {"item": "industrialreforged:iridium_ore"},
            },
            "pattern": ["RQR", "QIQ", "RQR"],
            "result": {"count": 1, "id": "industrialreforged:solar_core_1_level"},
        }
        json.dump(recipe, open(os.path.join(RECIPES, "shaped", "solar_core_1_level.json"), "w", encoding="utf-8"), indent=2)
        return

    recipe = {
        "type": "industrialreforged:molecular_transformer",
        "energy": 8_000_000 * 2 ** (level - 2),
        "input": {"item": f"industrialreforged:solar_core_{level - 1}_level"},
        "output": {"count": 1, "id": f"industrialreforged:solar_core_{level}_level"},
    }
    json.dump(recipe, open(os.path.join(RECIPES, "molecular_transformer", f"solar_core_{level}_level.json"), "w", encoding="utf-8"), indent=2)


def main():
    """v11: фотоэлементы крафтятся из предыдущего + материалы, солнечные ядра
    идут через молекулярный трансформер (плата 2M FE с удвоением за уровень).

    Панель: 3 панели прошлого уровня + фотоэлемент×2 + квантовое ядро/модуль +
    иридий×2 + солнечное ядро."""
    global photocell_energy
    photocell_energy = {level: 2_000_000 * 2 ** (level - 2) for level in range(2, 16)}
    for level in range(2, 16):
        write_panel(level)
        write_core(level)
        write_photocell(level)
    write_core(1)
    write_panel(1)

    economy.CRAFT.clear(); economy.MACHINE.clear(); economy.SMELTING.clear(); economy.TAGS.clear()
    economy.load_recipes()
    print("панели v11: 3 панели прошлого уровня + фотоэлементы (крафт) + ядра (MT):")
    for level in range(2, 16):
        print(f"  solar_core_{level}_level (MT): {8_000_000 * 2 ** (level - 2):,} FE")
    print()
    for level in range(1, 16):
        fe = economy.total_energy(f"industrialreforged:solar_generator_{level}_level")
        print(f"  панель {level:>2}: {fe:,} FE машинных операций")


if __name__ == "__main__":
    main()
