#!/usr/bin/env python3
"""Generates a machine test plan from the mod's recipe files.

Reads data/industrialreforged/recipe/<type>/*.json and picks, per recipe type,
the first recipe that is usable in a vanilla+mod environment (no
neoforge:conditions, plain item ingredient, plain item result).  The plan maps
every recipe type onto the machine block that processes it.

Usage: tools/irbridge_plan.py [--out tools/plan.json]
"""

import json
import os
import sys
import glob

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RECIPES = os.path.join(ROOT, "src/main/resources/data/industrialreforged/recipe")

# recipe type -> (machine block id, input container, extra requirements)
MACHINES = {
    "macerator": ("macerator_1_level", "input", {}),
    "compressor": ("compressor_1_level", "input", {}),
    "extractor": ("extractor_1_level", "input", {}),
    "centrifuge": ("centrifuge", "input", {"heat": True}),
    "block_cutter": ("block_cutter", "input", {"blade": "industrialreforged:iron_cutting_blade"}),
    "metal_former": ("metal_former_1_level", "input", {}),
    "blast_furnace": ("blast_furnace", "input", {"cells": "industrialreforged:compressed_air_cell", "heat": True, "long": True, "preheat": 50000}),
    "scanner": ("scanner", "input", {"disk": "industrialreforged:crystal_memory"}),
    "ore_washer": ("ore_washing_plant", "input", {"water": 5000}),
    "molecular_transformer": ("molecular_transformer", "input", {}),
    "fermenter": ("fermenter", "input", {"heat": True}),
    "condenser": ("condenser", "input", {"vent": "industrialreforged:heat_vent"}),
    "fluid_bottler": ("fluid_bottler", "fillInput", {}),
    "canner": ("canner", "canInput", {}),
    "solid_canner": ("solid_canner", "input", {"can": "industrialreforged:tin_can"}),
}

# Machines whose mechanics are coded (no datapack processing recipe exists and
# none is needed - same as in the JEI categories).  Their
# test cases are defined explicitly instead of by scanning recipe files.
#
# Extra keys understood by irbridge_machines.py:
#   input_slot     container to feed the item into (default: auto-detect)
#   skip_input     place no item input at all
#   fluid          {"fluid": id, "amount": n} - fill the machine tank first
#   mode           NBT "mode" value for the block entity (canner)
#   expected_fluid {"tank": i, "fluid": id, "amount": n} - pass on tank content
#   expected_container "outputSlot" - pass on a single named item container
CODED = {
    "fermenter": {
        "container": "biomassInput",
        "extra": {
            "heat": True,
            "skip_input": True,
            "fluid": {"fluid": "industrialreforged:biomass", "amount": 10000},
            "expected_fluid": {"tank": 1, "fluid": "industrialreforged:biogas", "amount": 1},
        },
        "input": "industrialreforged:biomass",
        "input_count": 1,
        "output": "industrialreforged:biogas",
        "output_count": 1,
    },
    "condenser": {
        "container": "waterInputSlot",
        "extra": {
            "vent": "industrialreforged:heat_vent",
            "skip_input": True,
            "fluid": {"fluid": "industrialreforged:steam", "amount": 10000},
            "expected_fluid": {"tank": 1, "fluid": "minecraft:water", "amount": 10000},
        },
        "input": "industrialreforged:steam",
        "input_count": 1,
        "output": "minecraft:water",
        "output_count": 100,
    },
    "fluid_bottler": {
        "container": "fillInput",
        "extra": {
            "input_slot": "fillInput",
            "fluid": {"fluid": "minecraft:water", "amount": 8000},
        },
        "input": "minecraft:bucket",
        "input_count": 4,
        "output": "minecraft:water_bucket",
        "output_count": 1,
    },
    "canner": {
        "container": "canInput",
        "extra": {
            "input_slot": "canInput",
            "fluid": {"fluid": "minecraft:water", "amount": 8000},
            "mode": 2,  # CannerBlockEntity.Mode.BottleLiquid
            "expected_container": "outputSlot",
        },
        # capsules are industrialreforged:cell with the industrialreforged:fluid
        # component; an empty cell carries no component at all
        "input": "industrialreforged:cell",
        "input_count": 4,
        "output": "industrialreforged:cell",
        "output_count": 1,
    },
    "solid_canner": {
        "container": "input",
        "extra": {"can": "industrialreforged:tin_can"},
        "input": "minecraft:potato",
        "input_count": 1,
        "output": "industrialreforged:filled_tin_can",
        "output_count": 1,
    },
}


def parse(recipe):
    """Extracts (input item, count, output item, count) or None."""
    if "neoforge:conditions" in recipe:
        return None

    if "cost" in recipe and "item" in recipe and isinstance(recipe["item"], dict):
        item_id = recipe["item"].get("id")
        if item_id:
            return (
                item_id,
                recipe["item"].get("count", 1),
                "industrialreforged:crystal_memory",
                1,
            )

    ingredient = recipe.get("ingredient") or recipe.get("input")
    if not isinstance(ingredient, dict) or "item" not in ingredient or "tag" in ingredient:
        return None

    result = recipe.get("result") or recipe.get("output")
    if isinstance(result, list):
        result = result[0] if result else None
    if not isinstance(result, dict) or "id" not in result:
        return None

    return (
        ingredient["item"],
        ingredient.get("count", 1),
        result["id"],
        result.get("count", 1),
    )


def pick(recipe_type):
    folder = os.path.join(RECIPES, recipe_type)
    if not os.path.isdir(folder):
        return None

    paths = sorted(glob.glob(os.path.join(folder, "**/*.json"), recursive=True))
    best = None
    for path in paths:
        try:
            recipe = json.load(open(path, encoding="utf-8"))
        except Exception:
            continue
        parsed = parse(recipe)
        if parsed is None:
            continue
        input_item, input_count, output, output_count = parsed
        # prefer recipes that need neither heat nor special cells
        weight = (1 if "minHeat" in recipe else 0) + (1 if "cells" in recipe else 0)
        if best is None or weight < best[0]:
            best = (
                weight,
                {
                    "recipe": os.path.relpath(path, RECIPES),
                    "input": input_item,
                    "input_count": input_count,
                    "output": output,
                    "output_count": output_count,
                },
            )
        if weight == 0:
            break

    return None if best is None else best[1]


def main():
    # Единственный источник плана: tools/plan.json (его читают tests_progression.py и irbridge_machines.py).
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), "plan.json")
    if "--out" in sys.argv:
        out = sys.argv[sys.argv.index("--out") + 1]

    plan = []
    for recipe_type, (block, container, extra) in MACHINES.items():
        coded = CODED.get(recipe_type)
        if coded is not None:
            plan.append({"type": recipe_type, "block": block, "status": "ready", **coded})
            continue

        picked = pick(recipe_type)
        if picked is None:
            plan.append({"type": recipe_type, "block": block, "status": "no usable recipe"})
            continue

        plan.append(
            {
                "type": recipe_type,
                "block": block,
                "container": container,
                "extra": extra,
                "status": "ready",
                **picked,
            }
        )

    with open(out, "w", encoding="utf-8") as handle:
        json.dump(plan, handle, indent=2)

    for entry in plan:
        if entry["status"] == "ready":
            print(f"{entry['type']:<22} {entry['block']:<24} {entry['input']} -> {entry['output']} x{entry['output_count']}")
        else:
            print(f"{entry['type']:<22} {entry['block']:<24} !! {entry['status']}")


if __name__ == "__main__":
    main()
