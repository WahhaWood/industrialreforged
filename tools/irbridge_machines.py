#!/usr/bin/env python3
"""Machine smoke test through the dev bridge.

For every entry of the plan (tools/irbridge_plan.py) it places the machine,
discovers its slots from the block entity NBT, feeds the recipe input (plus a
water bucket for machines that need it), charges the buffer, waits for the
output and reports PASS/FAIL with the observed numbers.

Usage: tools/irbridge_machines.py [--base http://127.0.0.1:9878] [--only TYPE]
"""

import json
import os
import sys
import time
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:9878"
PX = 200          # test area, blocks are spaced 8 apart
PZ = -400
PY = 100
ENERGY = 50_000_000
MAX_WAIT_TICKS = 1500
LONG_WAIT_TICKS = 7000
POLL_TICKS = 20


sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402


def call(path, payload=None, timeout=120):
    # BASE может поменять __main__ (--base), поэтому настраиваем мост при каждом вызове.
    bridgekit.configure(BASE)
    return bridgekit.call(path, payload, timeout)


def execs(*commands):
    return call("/exec", {"commands": list(commands)})


def items_at(x, y, z):
    body = call(f"/items?x={x}&y={y}&z={z}")
    return {entry["item"]: entry.get("count", 0) for entry in body.get("slots", [])}


def fluid_amount(x, y, z, tank, fluid):
    """Amount of `fluid` in /fluids tank index `tank`, 0 if it is something else."""
    body = call(f"/fluids?x={x}&y={y}&z={z}")
    tanks = body.get("tanks", [])
    if not body.get("found") or tank >= len(tanks):
        return 0
    entry = tanks[tank]
    return entry.get("amount", 0) if entry.get("fluid") == fluid else 0


def container_items(x, y, z, field):
    """Items of a single named container (field name) of the block entity."""
    body = call(f"/items?x={x}&y={y}&z={z}")
    entries = body.get("containers", {}).get(field, [])
    return {entry["item"]: entry.get("count", 0) for entry in entries}


def containers(be_snbt):
    """Very small SNBT scan: container name -> list of item ids inside."""
    result = {}
    index = 0
    while True:
        colon = be_snbt.find(":[", index)
        if colon < 0:
            break
        name_start = be_snbt.rfind(",", 0, colon)
        name_start = be_snbt.rfind("{", 0, colon) if name_start < 0 else name_start
        name = be_snbt[name_start + 1:colon].strip()
        # find the matching closing bracket
        depth = 0
        end = colon + 1
        for i in range(colon + 1, len(be_snbt)):
            if be_snbt[i] == "[":
                depth += 1
            elif be_snbt[i] == "]":
                depth -= 1
                if depth == 0:
                    end = i
                    break
        result[name] = be_snbt[colon + 2:end]
        index = end + 1
    return result


def run_case(plan, index):
    x = PX + index * 8
    z, y = PZ, PY
    name = plan["type"]
    block = plan["block"]
    machine = f"{block.replace(':', '')}"
    extra = plan.get("extra", {})

    execs(
        f"/forceload add {x - 8} {z - 8} {x + 8} {z + 8}",
        "/gamerule doDaylightCycle false",
        "/time set noon",
        f"/setblock {x} {y} {z} minecraft:air",
        f"/setblock {x} {y} {z} industrialreforged:{machine}",
    )

    block_info = call(f"/block?x={x}&y={y}&z={z}")
    if block_info.get("block") != f"industrialreforged:{machine}":
        return {"type": name, "status": "FAIL", "detail": f"block not placed ({block_info.get('block')})"}

    be = block_info.get("be") or ""
    slots = containers(be) if be else {}

    # figure out where the input goes and which slots should be checked
    input_slot = extra.get("input_slot")
    if input_slot is not None and input_slot not in slots:
        return {"type": name, "status": "FAIL", "detail": f"input slot {input_slot!r} not in {list(slots)}"}
    if input_slot is None and not extra.get("skip_input"):
        input_slot = "input" if "input" in slots else None
        if input_slot is None:
            for candidate in slots:
                if candidate.lower() in ("input", "contents") or "input" in candidate.lower():
                    input_slot = candidate
                    break
    if input_slot is None and not extra.get("skip_input"):
        return {"type": name, "status": "SKIP", "detail": f"no input slot found ({list(slots)})"}

    commands = []
    if input_slot is not None and not extra.get("skip_input"):
        input_item = plan["input"]
        input_count = max(1, plan.get("input_count", 1)) * 4
        commands.append(
            f"/data merge block {x} {y} {z} {{{input_slot}:[{{Slot:0b,id:\"{input_item}\",count:{input_count}}}]}}"
        )

    # water bucket for machines that run on water
    if plan.get("extra", {}).get("water"):
        fluid_slot = None
        for candidate in slots:
            if "fluid" in candidate.lower() or "water" in candidate.lower():
                if candidate != input_slot:
                    fluid_slot = candidate
                    break
        if fluid_slot is not None:
            commands.append(
                f"/data merge block {x} {y} {z} {{{fluid_slot}:[{{Slot:0b,id:\"minecraft:water_bucket\",count:1}}]}}"
            )

    if extra.get("blade"):
        commands.append(
            f'/data merge block {x} {y} {z} {{cutterInputSlot:[{{Slot:0b,id:"{extra["blade"]}",count:1}}]}}'
        )
    if extra.get("cells"):
        commands.append(
            f'/data merge block {x} {y} {z} {{cellInput:[{{Slot:0b,id:"{extra["cells"]}",count:16}}]}}'
        )
    if extra.get("disk"):
        commands.append(
            f'/data merge block {x} {y} {z} {{disk:[{{Slot:0b,id:"{extra["disk"]}",count:1}}]}}'
        )
    if extra.get("can"):
        commands.append(
            f'/data merge block {x} {y} {z} {{canInput:[{{Slot:0b,id:"{extra["can"]}",count:16}}]}}'
        )
    if extra.get("vent"):
        commands.append(
            f'/data merge block {x} {y} {z} {{ventSlots:[{{Slot:0b,id:"{extra["vent"]}",count:1}}]}}'
        )
    if extra.get("preheat"):
        commands.append(f"/data merge block {x} {y} {z} {{heat:{extra['preheat']}}}")
    if extra.get("mode") is not None:
        # routed through loadAdditional -> setMode(...) so the machine's mode
        # side effects (slot op types) are applied too
        commands.append(f"/data merge block {x} {y} {z} {{mode:{extra['mode']}}}")

    if extra.get("heat"):
        # Твёрдотопливный теплогенератор с севера от машины, повёрнутый к ней и разожжённый.
        # Раньше здесь клался только уголь в контейнер: контейнер сам по себе генератор не
        # зажигает, нужен burnTime — из-за этого печь/центрифуга/ферментер стояли холодными.
        commands.append(f"/setblock {x} {y} {z - 1} industrialreforged:solid_heat_generator[facing=south]")
        commands.append(
            f'/data merge block {x} {y} {z - 1} '
            '{fuel:[{Slot:0b,id:"minecraft:coal",count:16}],burnTime:1600,itemFuelTime:1600}'
        )

    if "energy" in be:
        commands.append(f"/data merge block {x} {y} {z} {{energy:{ENERGY}}}")
    commands.append(f"/data merge block {x} {y} {z} {{x:{x},y:{y},z:{z}}}")  # keep ids in place
    execs(*commands)

    if extra.get("fluid"):
        call("/fluids", {"x": x, "y": y, "z": z, **extra["fluid"]})

    expected = plan["output"]
    expected_count = max(1, plan.get("output_count", 1))
    expected_fluid = extra.get("expected_fluid")
    expected_container = extra.get("expected_container")
    needed = expected_fluid.get("amount", 1) if expected_fluid else expected_count
    waited = 0
    found = 0
    budget = LONG_WAIT_TICKS if extra.get("long") else MAX_WAIT_TICKS
    while waited < budget:
        call("/wait", {"ticks": POLL_TICKS})
        waited += POLL_TICKS
        if expected_fluid:
            found = fluid_amount(x, y, z, expected_fluid["tank"], expected_fluid["fluid"])
        elif expected_container:
            found = container_items(x, y, z, expected_container).get(expected, 0)
        else:
            found = items_at(x, y, z).get(expected, 0)
        if found >= needed:
            break

    energy = call(f"/energy?x={x}&y={y}&z={z}")
    if expected_fluid:
        progress = f"fluid {expected_fluid['fluid']} {found}/{needed} mB in tank {expected_fluid['tank']}"
    elif expected_container:
        progress = f"output {expected} x{found}/{needed} in {expected_container}"
    else:
        progress = f"output {expected} x{found}/{needed}"
    detail = (
        f"{progress} after {waited} ticks"
        + (f", stored={energy.get('storedLong', energy.get('stored'))}/{energy.get('capacityLong', energy.get('capacity'))}"
           if energy.get("found") else "")
        + f", slots={list(slots)}"
    )

    return {"type": name, "status": "PASS" if found >= needed else "FAIL", "detail": detail}


def main():
    global BASE
    if "--base" in sys.argv:
        BASE = sys.argv[sys.argv.index("--base") + 1]

    only = None
    if "--only" in sys.argv:
        only = sys.argv[sys.argv.index("--only") + 1]

    with open(os.path.join(os.path.dirname(os.path.abspath(__file__)), "plan.json"), encoding="utf-8") as handle:
        plan = json.load(handle)

    results = []
    for index, case in enumerate(plan):
        if case.get("status") != "ready":
            print(f"SKIP  {case['type']:<22} {case.get('status')}")
            continue
        if only and case["type"] != only:
            continue

        try:
            result = run_case(case, index)
        except Exception as error:  # noqa: BLE001 - report and continue
            result = {"type": case["type"], "status": "ERROR", "detail": str(error)}

        results.append(result)
        print(f"{result['status']:<5} {result['type']:<22} {result['detail']}")

    passed = sum(1 for r in results if r["status"] == "PASS")
    print()
    print(f"итого: {passed}/{len(results)} прошло")
    for result in results:
        if result["status"] != "PASS":
            print(f"  {result['status']}: {result['type']} — {result['detail']}")


if __name__ == "__main__":
    main()
