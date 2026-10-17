#!/usr/bin/env python3
"""IndustrialReforged dev bridge CLI.

The mod ships a small localhost HTTP bridge (dev builds only, port 9877) that
can run commands, read blocks/energy/fluids/items, drive machine menus by slot
index, dump a text map of an area and take screenshots.

Examples:
  tools/irbridge.py status
  tools/irbridge.py exec "/time set day"
  tools/irbridge.py execs "/give @p minecraft:coal 64" "/setblock 100 64 -200 industrialreforged:generator"
  tools/irbridge.py wait 40
  tools/irbridge.py block 100 64 -200
  tools/irbridge.py energy 100 64 -200 north
  tools/irbridge.py fluids 100 64 -200
  tools/irbridge.py items player
  tools/irbridge.py menu
  tools/irbridge.py open 100 64 -200
  tools/irbridge.py click 0
  tools/irbridge.py close
  tools/irbridge.py map 96 -204 104 -196 64
  tools/irbridge.py shot bridge
"""

import json
import os
import sys
import urllib.request

sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402

BASE = "http://127.0.0.1:9877"
bridgekit.configure(BASE)


def call(path, payload=None, timeout=60):
    try:
        return bridgekit.call(path, payload, timeout)
    except bridgekit.BridgeDown as error:
        print(f"bridge unreachable: {error}", file=sys.stderr)
        sys.exit(1)


def pretty(data):
    print(json.dumps(data, indent=2, ensure_ascii=False))


def main(argv):
    global BASE
    if "--base" in argv:
        index = argv.index("--base")
        BASE = argv[index + 1]
        bridgekit.configure(BASE)
        argv = argv[:index] + argv[index + 2:]

    command = argv[0] if argv else "status"
    args = argv[1:]

    if command == "status":
        pretty(call("/status"))
    elif command == "help":
        pretty(call("/help"))
    elif command == "exec":
        data = call("/exec", {"command": " ".join(args)}, timeout=120)
        for line in data.get("output", []):
            print(line)
        if not data.get("ok", True):
            print("FAILED:", data.get("error"))
            sys.exit(1)
    elif command == "execs":
        data = call("/exec", {"commands": args}, timeout=300)
        for line in data.get("output", []):
            print(line)
        if not data.get("ok", True):
            print("FAILED:", data.get("error"))
            sys.exit(1)
    elif command == "wait":
        pretty(call("/wait", {"ticks": int(args[0]) if args else 20}, timeout=300))
    elif command in ("block", "energy", "fluids"):
        x, y, z = args[0], args[1], args[2]
        side = f"&side={args[3]}" if len(args) > 3 else ""
        request = urllib.request.Request(f"{BASE}/{command}?x={x}&y={y}&z={z}{side}")
        with urllib.request.urlopen(request, timeout=30) as response:
            pretty(json.loads(response.read().decode("utf-8")))
    elif command == "items":
        if not args or args[0] == "player":
            request = urllib.request.Request(f"{BASE}/items?player")
        else:
            x, y, z = args[0], args[1], args[2]
            side = f"&side={args[3]}" if len(args) > 3 else ""
            request = urllib.request.Request(f"{BASE}/items?x={x}&y={y}&z={z}{side}")
        with urllib.request.urlopen(request, timeout=30) as response:
            pretty(json.loads(response.read().decode("utf-8")))
    elif command == "menu":
        pretty(call("/menu"))
    elif command == "open":
        pretty(call("/menu/open", {"x": int(args[0]), "y": int(args[1]), "z": int(args[2])}))
    elif command == "click":
        payload = {
            "slot": int(args[0]),
            "button": int(args[1]) if len(args) > 1 else 0,
            "type": args[2] if len(args) > 2 else "PICKUP",
        }
        pretty(call("/menu/click", payload))
    elif command == "close":
        pretty(call("/menu/close", {}))
    elif command == "map":
        x1, z1, x2, z2, y = (int(value) for value in args[:5])
        data = call(f"/map?x1={x1}&z1={z1}&x2={x2}&z2={z2}&y={y}")
        print("\n".join(data.get("rows", [])))
        legend = data.get("legend", {})
        print("legend: " + ", ".join(f"{key}={value}" for key, value in legend.items()))
    elif command == "shot":
        pretty(call("/screenshot", {"path": args[0] if args else "bridge"}))
    elif command == "players":
        pretty(call("/players"))
    else:
        print(__doc__, file=sys.stderr)
        sys.exit(2)


if __name__ == "__main__":
    main(sys.argv[1:])
