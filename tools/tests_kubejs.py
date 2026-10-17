#!/usr/bin/env python3
"""Проверка интеграции с KubeJS на живом сервере.

Что проверяем (и почему это важно именно для сервера):
  1. KubeJS + rhino установлены, сервер стартует с ними без ошибок;
  2. скрипт добавляет рецепт НАШЕГО машинного типа и машина его отрабатывает —
     главное доказательство, что типы рецептов мода доступны скриптам;
  3. скрипт убирает рецепт мода, и его действительно больше нет.

Скрипт для проверки кладёт сам набор: он пишет kubejs/server_scripts/recipes/ir_test.js
и просит сервер перечитать скрипты командой /kubejs reload server.

Если KubeJS не установлен — все проверки SKIP (мод обязан работать и без него).

Использование:
  python3 tools/tests_kubejs.py --base http://127.0.0.1:9879 [--keep-script]
"""

import json
import os
import sys
import time

import irbridge_testkit as t
from irbridge_testkit import check, execs, wait, call

SERVER_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "run-server")
SCRIPT_DIR = os.path.join(SERVER_DIR, "kubejs", "server_scripts", "recipes")
SCRIPT = os.path.join(SCRIPT_DIR, "ir_test.js")
Y, Z = 95, -300

SCRIPT_TEXT = """// Автотест KubeJS-интеграции IndustrialReforged (пишется tools/tests_kubejs.py)
ServerEvents.recipes(event => {
  event.custom({
    type: 'industrialreforged:macerator',
    ingredient: { item: 'minecraft:golden_apple' },
    result: { id: 'minecraft:glowstone_dust', count: 4 }
  })
  event.remove({ output: 'industrialreforged:circuit' })
})
"""


def kubejs_installed():
    mods = os.path.join(SERVER_DIR, "mods")
    if not os.path.isdir(mods):
        return False
    return any("kubejs" in name.lower() for name in os.listdir(mods))


def status():
    try:
        return call("/status")
    except Exception:       # noqa: BLE001
        return {}


def read_nbt(x):
    return call(f"/block?x={x}&y={Y}&z={Z}").get("be", "")


def clear_area(x):
    """Чистим площадку: setblock НЕ очищает контейнеры block entity, и остатки
    прошлого прогона (занятый выход, закэшированный рецепт) ломают измерение."""
    execs(f"fill {x - 2} {Y - 1} {Z - 2} {x + 2} {Y - 1} {Z + 2} minecraft:stone",
          f"fill {x - 2} {Y} {Z - 2} {x + 2} {Y} {Z + 2} minecraft:air")


def run_machine(x, item):
    clear_area(x)
    execs(f"setblock {x} {Y} {Z} industrialreforged:macerator_1_level")
    wait(10)
    execs(f'data merge block {x} {Y} {Z} ' + '{energy:900000}',
          f'data merge block {x} {Y} {Z} ' + '{input:[{Slot:0b,id:"' + item + '",count:4}]}')
    # операция мацератора 1 ур. — около 360 тиков, берём с запасом
    wait(700)
    return read_nbt(x)


def main():
    keep = "--keep-script" in sys.argv
    if "--base" in sys.argv:
        t.BASE = sys.argv[sys.argv.index("--base") + 1]

    if not kubejs_installed():
        print("SKIP: KubeJS не установлен в run-server/mods — мод обязан работать и без него")
        return 0

    alive, info = t.alive()
    if not alive:
        print(f"SKIP: сервер не отвечает ({info})")
        return 0
    if status().get("dist") != "CLIENT" and "kubejs" not in _server_log_tail(4000).lower():
        # сервер мог подняться до установки KubeJS — тогда проверка бессмысленна
        if "kubejs" not in _server_log_tail(4000).lower():
            print("SKIP: текущий сервер запущен без KubeJS — перезапусти его с модом в mods/")
            return 0

    os.makedirs(SCRIPT_DIR, exist_ok=True)
    with open(SCRIPT, "w", encoding="utf-8") as handle:
        handle.write(SCRIPT_TEXT)

    # В KubeJS 7 две разные команды: `kubejs reload server-scripts` перечитывает СКРИПТЫ,
    # а рецепты/теги/лут перечитывает ванильный `/reload` — именно об этом пишет сам KubeJS
    # в подсказке ("Done! To reload recipes ... run '/reload'").
    # Порядок важен: /reload перечитывает чанки и сбрасывает форсированную загрузку,
    # поэтому forceload возвращаем ПОСЛЕ перезагрузки, иначе машины просто не тикают
    # (machine_heat остаётся 0).
    execs("kubejs reload server-scripts", "/reload")
    wait(60)
    base = 200 + (int(time.time()) % 400) * 2
    execs(f"forceload add {base - 8} {Z - 10} {base + 12} {Z + 10}")
    wait(30)

    # Уникальные координаты на каждый запуск: стенд не должен наследовать блоки
    # от прошлых прогонов (контейнеры block entity переживают setblock).
    # 1. рецепт, добавленный скриптом, реально работает
    nbt = run_machine(base, "minecraft:golden_apple")
    check("рецепт, добавленный скриптом KubeJS, отрабатывает в машине",
          "glowstone_dust" in nbt, nbt[:160])

    # 2. обычный рецепт мода при этом не сломан
    nbt = run_machine(base + 4, "minecraft:iron_ore")
    check("обычный рецепт мода продолжает работать при KubeJS",
          "crushed_iron" in nbt, nbt[:160])

    # 3. скрипт убрал рецепт схемы
    recipes = call("/recipe", {"item": "industrialreforged:circuit"}).get("recipes", [])
    ids = [str(r.get("id")) for r in recipes]
    check("рецепт, удалённый скриптом, действительно исчез",
          not any("shaped/circuit" in i for i in ids), f"остались: {ids}")

    if not keep:
        try:
            os.remove(SCRIPT)
            execs("kubejs reload server-scripts", "/reload")
            wait(40)
        except OSError:
            pass
    execs(f"forceload remove {base - 8} {Z - 10} {base + 12} {Z + 10}")
    return t.summary("tests_kubejs")


def _server_log_tail(chars):
    path = os.path.join(SERVER_DIR, "logs", "latest.log")
    try:
        with open(path, encoding="utf-8", errors="replace") as handle:
            return handle.read()[-chars:]
    except OSError:
        return ""


if __name__ == "__main__":
    raise SystemExit(main())
