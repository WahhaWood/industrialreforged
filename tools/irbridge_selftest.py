#!/usr/bin/env python3
"""Self test for the IndustrialReforged dev bridge.

Exercises every endpoint (commands, blocks, energy, fluids, items, menus with a
fake player, map, screenshots, error handling) and prints PASS/FAIL per check.

Usage: tools/irbridge_selftest.py [--base http://127.0.0.1:9877]
"""

import json
import sys
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:9877"
RESULTS = []


def call(path, payload=None, timeout=90, method=None, raw=False):
    url = BASE + path
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json"})
    if method:
        request.get_method = lambda: method
    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            body = response.read().decode("utf-8")
            return response.status, (body if raw else json.loads(body))
    except urllib.error.HTTPError as error:
        body = error.read().decode("utf-8")
        try:
            return error.code, json.loads(body)
        except Exception:
            return error.code, {"raw": body}


def check(name, condition, extra=""):
    RESULTS.append((name, bool(condition)))
    print(f"{'PASS' if condition else 'FAIL'}  {name}" + (f"   [{extra}]" if extra else ""))


def clear_fake_inventory():
    """Empties the fake player inventory so the menu checks are exact."""
    _, body = call("/items?player=fake")
    for entry in body.get("slots") or []:
        call("/items", {"player": "fake", "slot": entry.get("slot", 0)})


def main():
    global BASE
    if "--base" in sys.argv:
        BASE = sys.argv[sys.argv.index("--base") + 1]

    # ---------------------------------------------------------------- status
    status_code, status = call("/status")
    check("GET /status returns 200 + mod id", status_code == 200 and status.get("mod") == "industrialreforged",
          f"dist={status.get('dist')} server={status.get('server')}")

    help_code, help_body = call("/help")
    check("GET /help lists endpoints", help_code == 200 and len(help_body.get("endpoints", [])) > 8)

    # ---------------------------------------------------------------- exec
    code, body = call("/exec", {"command": "/time set day"})
    output = " | ".join(body.get("output", []))
    check("POST /exec captures command feedback", code == 200 and "Set the time" in output, output[:70])

    code, body = call("/exec", {"command": "/gamerule doDaylightCycle false"})
    check("POST /exec runs a second command", code == 200 and any("doDaylightCycle" in line for line in body.get("output", [])))

    code, body = call("/exec", {"command": "/give"})
    output = " | ".join(body.get("output", []))
    check("POST /exec reports command errors", code == 200 and ("give" in output.lower() or "аргумент" in output.lower()), output[:70])

    code, body = call("/exec", {"commands": ["/forceload add 304 -208 320 -196", "/setblock 310 64 -200 minecraft:stone"]})
    check("POST /exec accepts a command list", code == 200 and len(body.get("output", [])) >= 2)

    # ---------------------------------------------------------------- wait
    before = call("/status")[1].get("tick", 0)
    code, body = call("/wait", {"ticks": 20, "timeoutMs": 30000}, timeout=60)
    after = call("/status")[1].get("tick", 0)
    check("POST /wait advances at least the asked ticks", code == 200 and after - before >= 20, f"tick {before} -> {after}")

    # ---------------------------------------------------------------- build rig
    rig = [
        "/setblock 306 64 -200 industrialreforged:generator",
        "/setblock 307 64 -200 industrialreforged:wire_3_level",
        "/setblock 308 64 -200 industrialreforged:macerator_1_level",
        "/setblock 310 64 -204 industrialreforged:solar_distiller",
        "/data merge block 306 64 -200 {fuel:[{Slot:0b,id:\"minecraft:coal\",count:16}]}",
        "/data merge block 308 64 -200 {input:[{Slot:0b,id:\"minecraft:iron_ore\",count:8}]}",
    ]
    code, body = call("/exec", {"commands": rig})
    check("POST /exec builds the test rig", code == 200 and len(body.get("output", [])) >= len(rig))

    # ---------------------------------------------------------------- block
    code, body = call("/block?x=308&y=64&z=-200")
    check("GET /block returns state + block entity SNBT", code == 200 and body.get("be") is not None and "macerator" in body.get("block", ""),
          body.get("block", ""))

    code, body = call("/block?x=310&y=63&z=-204")
    check("GET /block on plain terrain has no block entity", code == 200 and "be" not in body)

    # ---------------------------------------------------------------- energy
    code, body = call("/wait", {"ticks": 40, "timeoutMs": 60000}, timeout=90)
    code, body = call("/energy?x=308&y=64&z=-200")
    stored = body.get("storedLong", body.get("stored", 0))
    check("GET /energy reads a machine buffer (wire feeds it)", code == 200 and body.get("found") and stored > 0, f"stored={stored}")

    code, body = call("/energy?x=310&y=64&z=-204")
    check("GET /energy on a machine without energy reports found=false", code == 200 and body.get("found") is False)

    # ---------------------------------------------------------------- fluids
    code, body = call("/fluids?x=310&y=64&z=-204")
    tanks = body.get("tanks") or []
    check("GET /fluids lists tanks of the distiller", code == 200 and body.get("found") and len(tanks) >= 2,
          f"tanks={len(tanks)}")

    # ---------------------------------------------------------------- items
    code, body = call("/items?x=308&y=64&z=-200")
    slots = body.get("slots") or []
    check("GET /items lists the machine input", code == 200 and any(s.get("item") == "minecraft:iron_ore" for s in slots))

    code, body = call("/items", {"player": "fake", "slot": 9, "item": "minecraft:iron_ore", "count": 7})
    check("POST /items writes into the fake player inventory", code == 200 and body.get("ok") and body.get("count") == 7)

    code, body = call("/items?player=fake")
    slots = body.get("slots") or []
    check("GET /items?player=fake shows the written stack", code == 200 and any(s.get("item") == "minecraft:iron_ore" for s in slots))

    # ---------------------------------------------------------------- menus
    # a chest is a stable menu target: nothing is ever consumed by machine logic
    clear_fake_inventory()
    code, body = call("/exec", {"command": "/setblock 312 64 -200 minecraft:chest"})
    check("POST /exec placed a chest for the menu test", code == 200)

    code, body = call("/menu/open", {"x": 312, "y": 64, "z": -200, "player": "fake"})
    check("POST /menu/open (fake player) opens the chest menu", code == 200 and body.get("ok") and body.get("slotCount") == 63,
          f"slotCount={body.get('slotCount')}")

    code, body = call("/menu", {"player": "fake"})
    player_start = body.get("playerInventoryStart", -1)
    check("GET /menu dumps slots + playerInventoryStart", code == 200 and player_start == 27, f"playerInventoryStart={player_start}")

    code, body = call("/items", {"player": "fake", "slot": 9, "item": "minecraft:iron_ore", "count": 7})
    check("POST /items writes into the fake player inventory", code == 200 and body.get("count") == 7)
    code, body = call("/items?player=fake")
    check("GET /items?player=fake shows the written stack",
          code == 200 and sum(s.get("count", 0) for s in (body.get("slots") or []) if s.get("item") == "minecraft:iron_ore") == 7)

    code, body = call("/menu/click", {"slot": player_start, "button": 0, "type": "PICKUP", "player": "fake"})
    check("POST /menu/click PICKUP picks the stack up", code == 200 and body.get("ok") and "iron_ore" in str(body.get("carried", "")),
          f"carried={body.get('carried')}")

    code, body = call("/menu/click", {"slot": 0, "button": 0, "type": "PICKUP", "player": "fake"})
    check("POST /menu/click PICKUP places it into the container", code == 200 and body.get("ok") and not body.get("carried"))

    code, body = call("/items?x=312&y=64&z=-200")
    in_chest = sum(s.get("count", 0) for s in (body.get("slots") or []) if s.get("item") == "minecraft:iron_ore")
    code, body = call("/items?player=fake")
    left = sum(s.get("count", 0) for s in (body.get("slots") or []) if s.get("item") == "minecraft:iron_ore")
    check("item really moved into the container", in_chest == 7, f"chest={in_chest}")
    check("no items were voided by the transfer", in_chest + left == 7, f"total={in_chest + left}")

    # quick move the other way: chest -> player
    code, body = call("/menu/click", {"slot": 0, "button": 0, "type": "QUICK_MOVE", "player": "fake"})
    check("POST /menu/click QUICK_MOVE moves it back", code == 200 and body.get("ok"))
    code, body = call("/items?x=312&y=64&z=-200")
    in_chest = sum(s.get("count", 0) for s in (body.get("slots") or []) if s.get("item") == "minecraft:iron_ore")
    code, body = call("/items?player=fake")
    left = sum(s.get("count", 0) for s in (body.get("slots") or []) if s.get("item") == "minecraft:iron_ore")
    check("QUICK_MOVE moved the stack back to the player", in_chest == 0 and left == 7, f"chest={in_chest} player={left}")

    code, body = call("/menu/close", {"player": "fake"})
    check("POST /menu/close closes the fake menu", code == 200 and body.get("ok"))

    # ---------------------------------------------------------------- map
    code, body = call("/map?x1=305&z1=-202&x2=310&z2=-198&y=64")
    check("GET /map returns a text map + legend", code == 200 and len(body.get("rows", [])) >= 5 and body.get("legend"))

    code, body = call("/map?x1=0&z1=0&x2=500&z2=500&y=64")
    check("GET /map rejects oversized areas", code == 400 and not body.get("ok", True), str(body.get("error"))[:60])

    # ---------------------------------------------------------------- misc
    code, body = call("/screenshot", {})
    if status.get("server"):
        check("POST /screenshot on a dedicated server fails cleanly", code in (400, 500) and not body.get("ok", True),
              str(body.get("error"))[:60])
    else:
        check("POST /screenshot on the client writes a png", code == 200 and body.get("ok"), str(body.get("path"))[:80])

    code, body = call("/nope")
    check("unknown endpoint returns 404", code == 404 and not body.get("ok", True))

    failed = [name for name, ok in RESULTS if not ok]
    print()
    print(f"{len(RESULTS) - len(failed)}/{len(RESULTS)} checks passed")
    if failed:
        print("failed:")
        for name in failed:
            print("  -", name)
        sys.exit(1)


if __name__ == "__main__":
    main()
