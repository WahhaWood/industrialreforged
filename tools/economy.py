#!/usr/bin/env python3
"""Recursive crafting-cost calculator over the mod's recipes.

Resolves an item into raw materials, machine operations and energy.  Crafting
recipes are preferred (they cost no energy), machine recipes contribute their
energy per operation (values taken from the block entity constructors).

Usage: tools/economy.py [item ...]
"""

import glob
import json
import os
import sys
from collections import Counter

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATAPACK = os.path.join(ROOT, "src/main/resources/data")
RECIPES = os.path.join(DATAPACK, "industrialreforged/recipe")

# machine recipe folder -> (energy per operation, FE considered "processing")
MACHINE_ENERGY = {
    "macerator": 900,
    "compressor": 1800,
    "extractor": 1800,
    "metal_former": 2000,
    "block_cutter": 900,
    "centrifuge": 48000,
    "blast_furnace": 900,
    "ore_washer": 5000,
    "molecular_transformer": 0,  # energy comes from the recipe itself
    "shapeless": 0,
}
SMELTING_ENERGY = 200

CRAFT = {}
MACHINE = {}
SMELTING = {}
TAGS = {}
OPS = Counter()
ENERGY = Counter()


def load_recipes():
    # crafting: папки shaped/shapeless + файлы прямо в recipe/ (провода лежат там,
    # без них калькулятор считал провода сырьём и занижал стоимость всего, что их содержит)
    for folder in ("shaped", "shapeless", ""):
        for path in glob.glob(os.path.join(RECIPES, folder, "**/*.json"), recursive=True):
            try:
                recipe = json.load(open(path, encoding="utf-8"))
                if not isinstance(recipe.get("result"), dict):
                    continue
            except Exception:
                continue
            if "neoforge:conditions" in recipe:
                continue
            result = recipe.get("result")
            if not isinstance(result, dict):
                continue
            output = result["id"]
            parts = []
            if "pattern" in recipe:
                counts = Counter(c for row in recipe["pattern"] for c in row if c != " ")
                for key, count in counts.items():
                    entry = recipe["key"].get(key)
                    if not isinstance(entry, dict):
                        continue
                    if "item" in entry:
                        parts.append((entry["item"], count))
                    elif "tag" in entry:
                        parts.append(("tag:" + entry["tag"], count))
            else:
                for entry in recipe.get("ingredients", []):
                    if not isinstance(entry, dict):
                        continue
                    if "item" in entry:
                        parts.append((entry["item"], entry.get("count", 1)))
                    elif "tag" in entry:
                        parts.append(("tag:" + entry["tag"], entry.get("count", 1)))
            if parts and output not in CRAFT:
                CRAFT[output] = (parts, result.get("count", 1))

    # machine recipes (first matching recipe per output, cheapest wins roughly)
    for machine, energy in MACHINE_ENERGY.items():
        folder = os.path.join(RECIPES, machine)
        if not os.path.isdir(folder):
            continue
        for path in glob.glob(os.path.join(folder, "**/*.json"), recursive=True):
            try:
                recipe = json.load(open(path, encoding="utf-8"))
            except Exception:
                continue
            if "neoforge:conditions" in recipe:
                continue
            ingredient = recipe.get("ingredient") or recipe.get("input")
            result = recipe.get("result") or recipe.get("output")
            if not isinstance(ingredient, dict) or not isinstance(result, dict):
                continue
            if "tag" in ingredient:
                source = "tag:" + ingredient["tag"]
            elif "item" in ingredient:
                source = ingredient["item"]
            else:
                continue
            output = result["id"]
            cost = recipe.get("energy", energy)
            if output not in MACHINE or MACHINE[output][0] > cost:
                MACHINE[output] = (cost, source, ingredient.get("count", 1), result.get("count", 1))

    # smelting
    folder = os.path.join(RECIPES, "smelting")
    for path in glob.glob(os.path.join(folder, "*.json")):
        try:
            recipe = json.load(open(path, encoding="utf-8"))
        except Exception:
            continue
        ingredient = recipe.get("ingredient", {})
        result = recipe.get("result")
        if isinstance(ingredient, dict) and "item" in ingredient and isinstance(result, str):
            SMELTING.setdefault(result, ingredient.get("item"))

    # item tags
    for path in glob.glob(os.path.join(DATAPACK, "*", "tags/item/**/*.json"), recursive=True):
        parts = path.split(os.sep)
        namespace = parts[parts.index("data") + 1]
        relative = "/".join(parts[parts.index("item") + 1:]).removesuffix(".json")
        tag = f"{namespace}:{relative}"
        try:
            data = json.load(open(path, encoding="utf-8"))
        except Exception:
            continue
        values = []
        for value in data.get("values", []):
            if isinstance(value, str):
                values.append(value)
            elif isinstance(value, dict) and "id" in value:
                values.append(value["id"])
        if values:
            TAGS.setdefault(tag, values)


# things a player simply mines or farms: never worth routing through machines
RAW_ITEMS = {
    "minecraft:diamond", "minecraft:emerald", "minecraft:lapis_lazuli", "minecraft:redstone",
    "minecraft:glowstone_dust", "minecraft:glowstone", "minecraft:quartz", "minecraft:coal",
    "minecraft:charcoal", "minecraft:flint", "minecraft:sand", "minecraft:gravel", "minecraft:clay_ball",
    "minecraft:nether_star", "minecraft:blaze_rod", "minecraft:blaze_powder", "minecraft:gunpowder",
    "minecraft:ender_pearl", "minecraft:leather", "minecraft:cobblestone", "minecraft:stone",
    "minecraft:iron_ingot", "minecraft:copper_ingot", "minecraft:gold_ingot",
}


UNPACK = {
    "minecraft:redstone_block": "minecraft:redstone",
    "minecraft:lapis_block": "minecraft:lapis_lazuli",
    "minecraft:gold_block": "minecraft:gold_ingot",
    "minecraft:iron_block": "minecraft:iron_ingot",
    "minecraft:coal_block": "minecraft:coal",
    "minecraft:diamond_block": "minecraft:diamond",
    "minecraft:copper_block": "minecraft:copper_ingot",
}


def resolve(item, amount, depth=0, stack=()):
    if depth > 40 or amount <= 0:
        return
    if item in stack:
        return
    if item in RAW_ITEMS:
        RAW[item] += amount
        return
    if item in UNPACK:
        resolve(UNPACK[item], 9 * amount, depth + 1, stack + (item,))
        return
    if item in CRAFT:
        parts, out_count = CRAFT[item]
        crafts = (amount + out_count - 1) // out_count
        for part, count in parts:
            resolve_part(part, count * crafts, depth + 1, stack + (item,))
        return
    if item in MACHINE:
        cost, source, count, out_count = MACHINE[item]
        crafts = (amount + out_count - 1) // out_count
        OPS[item] += crafts
        ENERGY["machine"] += cost * crafts
        resolve_part(source, count * crafts, depth + 1, stack + (item,))
        return
    if item in SMELTING and SMELTING[item]:
        ENERGY["smelting"] += SMELTING_ENERGY * amount
        resolve(SMELTING[item], amount, depth + 1, stack + (item,))
        return
    RAW[item] += amount


def resolve_part(part, amount, depth, stack):
    if part.startswith("tag:"):
        tag = part[4:]
        values = [v for v in TAGS.get(tag, []) if not v.startswith("#")]
        preferred = [v for v in values if v.startswith("industrialreforged:")] or values
        if not preferred:
            RAW["tag:" + tag] += amount
            return
        resolve(preferred[0], amount, depth, stack)
        return
    resolve(part, amount, depth, stack)


RAW = Counter()


def total_energy(item):
    """Machine + smelting energy needed for one item."""
    global RAW, OPS, ENERGY
    RAW = Counter()
    OPS = Counter()
    ENERGY = Counter()
    resolve(item, 1)
    return ENERGY["machine"] + ENERGY["smelting"]


def report(item, fe_power_options):
    global RAW, OPS, ENERGY
    RAW = Counter()
    OPS = Counter()
    ENERGY = Counter()
    resolve(item, 1)

    total_energy = ENERGY["machine"] + ENERGY["smelting"]
    print(f"=== {item} ===")
    print(f"  энергия машин: {total_energy:,} FE" + (f" (в т.ч. MT: {ENERGY['machine']:,} FE)" if ENERGY["machine"] else ""))
    print(f"  операций машин: {sum(OPS.values()):,}")
    top = ", ".join(f"{k.split(':')[-1]}×{v}" for k, v in RAW.most_common(12))
    print(f"  сырьё (топ): {top}")
    for power in fe_power_options:
        seconds = total_energy / power
        print(f"    при {power:,} FE/t -> {seconds/3600:,.1f} ч ({seconds/86400:,.1f} суток)")


def main():
    load_recipes()
    items = sys.argv[1:] or [
        "industrialreforged:solar_generator_1_level",
        "industrialreforged:solar_generator_5_level",
        "industrialreforged:solar_generator_6_level",
        "industrialreforged:solar_generator_9_level",
        "industrialreforged:solar_generator_15_level",
    ]
    powers = [20_000, 128_000, 15_625_000, 100_000_000_000]
    for item in items:
        report(item, powers)


if __name__ == "__main__":
    main()
