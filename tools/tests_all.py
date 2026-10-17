#!/usr/bin/env python3
"""Раннер всех e2e-сюит: python3 tools/tests_all.py [--base http://127.0.0.1:9878]

Раннер отвечает за изоляцию, а наборы — за проверки:

* перед каждым набором снимается вся форсированная загрузка (`forceload remove all`
  в оверворлде и Нижнем мире), иначе чанки прошлых прогонов продолжают тикать;
* после набора его объём чистится блоками и с него снимается форсированная
  загрузка — иначе машины с энергией/стержнями продолжают работать до конца
  прогона, а предметы копятся;
* набор выполняется в отдельном потоке с собственным таймаутом, так что зависший
  сервер даёт диагностику, а не молчание;
* падение набора не роняет прогон: в конце печатается сводка с номерами и именами
  упавших наборов.
"""

import sys
import threading
import time

import check_assets
import check_nbt
import check_registry_ids
import irbridge_testkit as t
import tests_advancements
import tests_boiler
import tests_controller
import tests_actions
import tests_crops
import tests_gui_sync
import tests_drops
import tests_fluids
import tests_kubejs
import tests_me
import tests_heat
import tests_panels
import tests_reactor
import tests_rotors
import tests_rubber
import tests_sided
import tests_strengthening
import tests_tank
import tests_tank_break
import tests_tank_valve_facing
import tests_teleporter

# Таймаут на набор. Считаем сумму wait() в наборах:
#   тепловые машины ~3200 тиков, укрепление ~1700, панели ~1100, роторы ~700,
#   остальные меньше. При 20 TPS это 80-160 с, но под нагрузкой (машина делит
#   ядра с другими dev-серверами) тики идут в разы медленнее, поэтому запас
#   x10. Переопределяется --timeout.
SUITE_TIMEOUT = 1800.0

# Пауза между наборами: даём серверу отдышаться после кучи fill/forceload
# и дождаться, пока разгрузятся чанки предыдущей площадки. Бюджет ожидания
# задан явно и короткий: если тики не идут, пауза не должна съедать минуту.
SETTLE_TICKS = 40
SETTLE_BUDGET = 30.0


class Suite:
    """Описание одного набора: модуль, заголовок, площадка для уборки."""

    def __init__(self, title, module, area=None, y_range=None, nether=None):
        self.title = title
        self.module = module
        self.area = area  # (x1, z1, x2, z2) в оверворлде
        self.y_range = y_range  # (y1, y2) — чистится после набора
        self.nether = nether  # (x1, z1, x2, z2) в Нижнем мире


SUITES = []


def suite(title, module, area=None, y_range=None, nether=None):
    SUITES.append(Suite(title, module, area, y_range, nether))


# Уборочные команды не должны сами висеть по 120 с, если сервер уже мёртв:
# на них короткий таймаут, чтобы раннер дошёл до сводки за секунды.
CLEANUP_TIMEOUT = 20.0


def unforce_all():
    """Снимает форсированную загрузку в обоих измерениях.

    `/forceload remove all` идемпотентен: если ничего не загружено, команда
    просто ничего не делает и возвращает ok.
    """
    try:
        t.execs("forceload remove all",
                "execute in minecraft:the_nether run forceload remove all",
                timeout=CLEANUP_TIMEOUT)
    except t.BridgeDown as error:
        print(f"  ! не снять форсированную загрузку: {error}")


def cleanup_suite(suite_obj):
    """Убирает за набором: блоки, предметы, форсированную загрузку."""
    if suite_obj.area is None:
        return

    x1, z1, x2, z2 = suite_obj.area
    y1, y2 = suite_obj.y_range or (-60, 320)
    try:
        # Порядок важен: сначала убиваем предметы (дешёвая команда), потом
        # заливаем объём air — это убирает block entity, иначе машины с
        # залитой энергией/стержнями тикают до конца прогона.
        t.kill_items(x1, max(y1, -59), z1, x2, y2, z2, timeout=CLEANUP_TIMEOUT)
        t.clear_volume(x1, y1, z1, x2, y2, z2, timeout=CLEANUP_TIMEOUT)
        if suite_obj.nether:
            nx1, nz1, nx2, nz2 = suite_obj.nether
            t.execs(f"execute in minecraft:the_nether run "
                    f"kill @e[type=item,x={nx1},y=95,z={nz1},"
                    f"dx={nx2 - nx1},dy=10,dz={nz2 - nz1}]",
                    timeout=CLEANUP_TIMEOUT)
            t.execs(f"execute in minecraft:the_nether run "
                    f"fill {nx1} 99 {nz1} {nx2} 99 {nz2} minecraft:air",
                    timeout=CLEANUP_TIMEOUT)
            t.execs(f"execute in minecraft:the_nether run forceload remove "
                    f"{nx1} {nz1} {nx2} {nz2}", timeout=CLEANUP_TIMEOUT)
        t.execs(f"forceload remove {x1} {z1} {x2} {z2}", timeout=CLEANUP_TIMEOUT)
    except t.BridgeDown as error:
        print(f"  ! уборка не завершена: {error}")


def run_suite(index, suite_obj, results):
    """Запускает набор в потоке с таймаутом. Никогда не бросает наружу."""
    print(f"\n== [{index}/{len(SUITES)}] {suite_obj.title} ==")
    started = time.monotonic()

    result = {}

    def body():
        try:
            result["code"] = suite_obj.module.main()
        except BaseException as error:  # noqa: BLE001 - раннер не должен падать
            result["error"] = error

    worker = threading.Thread(target=body, daemon=True, name=f"suite-{index}")
    worker.start()
    worker.join(SUITE_TIMEOUT)

    elapsed = time.monotonic() - started

    if worker.is_alive():
        message = (f"набор не уложился в {SUITE_TIMEOUT:g} с — вероятно, сервер "
                   f"перестал тикать; поток оставлен висеть, он daemon")
        results.append((index, suite_obj, "TIMEOUT", message, elapsed, []))
        print(f"  !! {message}")
        return

    error = result.get("error")
    if error is not None:
        if isinstance(error, t.BridgeDown):
            status, message = "BRIDGE DOWN", str(error)
        else:
            status = "CRASH"
            message = f"{type(error).__name__}: {error}"
        fails = t.take_pending_fails()
        results.append((index, suite_obj, status, message, elapsed, fails))
        print(f"  !! набор упал: {message}")
        for name in fails:
            print("  FAIL:", name)
        return

    fails = list(t.LAST["fails"])
    code = result.get("code", 0)
    status = "ok" if code == 0 else "FAIL"
    results.append((index, suite_obj, status, "", elapsed, fails))


def main():
    args = sys.argv[1:]
    if "--base" in args:
        t.BASE = args[args.index("--base") + 1]
    if "--timeout" in args:
        globals()["SUITE_TIMEOUT"] = float(args[args.index("--timeout") + 1])

    # Статика идёт первым и без dev-моста: сломанный JSON или грань, уехавшая
    # внутрь блока, ловятся до запуска сервера, а не после него.
    print("== ассеты: blockstates, модели, текстуры ==")
    code = check_assets.main()
    print()

    print("== NBT: симметрия ключей во всех block entity ==")
    nbt_code = check_nbt.main()
    print()

    # Удаление или перенос registry id ломает уже сгенерированный мир: блок
    # становится воздухом, а в прегенерированных чанках новый блок не появится.
    print("== registry id: ничего не удалено и не переименовано ==")
    registry_code = check_registry_ids.main()
    print()
    if registry_code:
        print("ВНИМАНИЕ: registry id разошлись с эталоном. Если мир уже прегенерирован —")
        print("          он поедет. Либо верни id, либо обнови эталон осознанно:")
        print("          python3 tools/check_registry_ids.py --update")
        print()

    alive, info = t.alive()
    if not alive:
        print(f"СЕРВЕР НЕ ОТВЕЧАЕТ: {info} ({t.BASE})")
        print("Подними выделенный сервер (./gradlew runServer) и повтори прогон.")
        return code or nbt_code or registry_code or 2

    print(f"сервер жив: {info}, {t.BASE}")

    results = []
    started = time.monotonic()

    for index, suite_obj in enumerate(SUITES, start=1):
        # Изоляция: чужой форсилоад прошлых наборов мог остаться в любой момент.
        unforce_all()
        if index > 1:
            try:
                t.wait(SETTLE_TICKS, budget=SETTLE_BUDGET)
            except t.BridgeDown as error:
                print(f"  ! пауза перед набором не прошла: {error}")
        run_suite(index, suite_obj, results)

        cleanup_suite(suite_obj)
        # Сервер мог умереть внутри набора — дальше идти бессмысленно,
        # но мы не падаем: оставшиеся наборы просто отметим как SKIPPED.
        alive, info = t.alive()
        if not alive:
            print(f"  !! сервер перестал отвечать: {info}")
            for rest_index in range(index + 1, len(SUITES) + 1):
                results.append((rest_index, SUITES[rest_index - 1], "SKIPPED",
                                "сервер не отвечает", 0.0, []))
            break

    total = time.monotonic() - started
    unforce_all()

    failed = [r for r in results if r[2] in ("FAIL", "TIMEOUT", "BRIDGE DOWN", "CRASH")]
    skipped = [r for r in results if r[2] == "SKIPPED"]
    ok_count = sum(1 for r in results if r[2] == "ok")

    print()
    print("=" * 70)
    print("СВОДКА НАБОРОВ")
    print("=" * 70)
    for index, suite_obj, status, message, elapsed, fails in results:
        mark = {"ok": "  OK  ", "FAIL": " FAIL ", "TIMEOUT": "TIMEOT",
                "BRIDGE DOWN": " MOСТ ", "CRASH": "CRASH", "SKIPPED": "SKIP "}[status]
        line = f"  {mark} [{index:2d}/{len(SUITES)}] {suite_obj.title} ({elapsed:6.1f} с)"
        if status != "ok":
            line += f"  — {status}: {message}" if message else f"  — {status}"
        print(line)
        for name in fails:
            print(f"           FAIL: {name}")

    print()
    print(f"итого: прошло {ok_count}, упало {len(failed)}, "
          f"не запустилось (сервер умер) {len(skipped)} из {len(SUITES)}")
    if failed:
        numbers = [r[0] for r in failed]
        print(f"УПАЛО НАБОРОВ: {len(failed)} — номера {numbers}")
        for index, suite_obj, status, message, _, _ in failed:
            print(f"  [{index}] {suite_obj.title}: {status} — {message}")
    else:
        print(f"УПАВШИХ НАБОРОВ НЕТ ({ok_count} из {len(SUITES)} прошли)")
    if skipped:
        print(f"НЕ ЗАПУЩЕНЫ (сервер перестал отвечать): {[r[0] for r in skipped]}")
    print(f"время прогона: {total:.1f} с")

    if code:
        return code
    return 1 if failed else 0


# Наборы. Площадки (area/y_range) — ровно те прямоугольники, которые набор
# заливает через fill/forceload, чтобы уборка не задела чужое.
suite("тепловые машины", tests_heat, area=(230, -420, 320, -380), y_range=(95, 110))
suite("достижения: дерево зарегистрировано", tests_advancements)
suite("улучшение «Укрепление»: 4 предмета за операцию", tests_strengthening,
      area=(230, -380, 280, -350), y_range=(195, 205))
suite("реактор: камера вне ядра и расширение", tests_reactor,
      area=(430, -420, 470, -390), y_range=(95, 105))
suite("телепортер: связь и телепорт", tests_teleporter,
      area=(430, -380, 470, -350), y_range=(195, 205), nether=(95, 95, 105, 105))
suite("дропы дорогих машин без ключа", tests_drops,
      area=(480, -420, 520, -390), y_range=(95, 105))
suite("солнечные панели", tests_panels,
      area=(230, -420, 320, -380), y_range=(175, 190))
suite("роторы и турбины", tests_rotors,
      area=(230, -420, 320, -380), y_range=(145, 265))
suite("стороны и автоматизация", tests_sided,
      area=(250, -410, 280, -380), y_range=(95, 105))
suite("резервуар для пара и воды (мультиблок)", tests_tank,
      area=(270, -415, 310, -385), y_range=(145, 175))
suite("резервуар: распад и активные текстуры стен", tests_tank_break,
      area=(335, -405, 360, -390), y_range=(145, 160))
suite("клапан резервуара: свойство FACING", tests_tank_valve_facing,
      area=(330, -415, 370, -385), y_range=(145, 175))
suite("гевея: отработанная древесина", tests_rubber,
      area=(236, -404, 244, -396), y_range=(95, 105))
suite("резервуар-котёл: вода+тепло -> пар", tests_boiler,
      area=(335, -405, 365, -385), y_range=(145, 165))
suite("контроллер резервуара: зеркало и GUI", tests_controller,
      area=(395, -415, 425, -395), y_range=(145, 165))
suite("посевы: посадка, рост, скрещивание", tests_crops,
      area=(244, -336, 256, -324), y_range=(95, 105))
suite("действия игрока не роняют сервер", tests_actions,
      area=(174, -286, 186, -274), y_range=(85, 95))
suite("жидкости: каждая в каждый бак", tests_fluids,
      area=(164, -266, 176, -254), y_range=(85, 95))
# SKIP, если KubeJS не установлен: мод обязан работать и без него
suite("KubeJS: рецепты из скриптов работают", tests_kubejs)
# набор живёт на клиенте: на выделенном сервере все проверки SKIP
suite("синхронизация GUI: что видит экран", tests_gui_sync,
      area=(234, -306, 254, -294), y_range=(85, 92))
# ME-часть SKIP без AE2 (localRuntime в build.gradle)
suite("утилизатор, генератор материи, ME-машины", tests_me,
      area=(600, -600, 660, -560), y_range=(196, 205))


if __name__ == "__main__":
    raise SystemExit(main())