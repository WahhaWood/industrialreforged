#!/usr/bin/env python3
"""Общий каркас e2e-тестов через dev-мост (по образцу ThaumicReforged tests_progress.py).

Каждый tests_*.py собирает проверки через check(), раннер tests_all.py сводит итог.

Правила, которые держит каркас:
* каждый HTTP-запрос имеет таймаут, а «мост не отвечает» — это исключение
  BridgeDown с внятным текстом, а не тихий {"ok": false};
* wait() дробит ожидание на короткие срезы и следит за /status, поэтому зависший
  сервер обнаруживается за секунды, а не через десятки минут молчания;
* хелперы площадки (clear_volume, kill_items, LAST/take_pending_fails) — ими
  изолирует наборы раннер tests_all.py.
"""

import json
import os
import sys
import time

# Транспорт, BridgeDown и харнес check/summary общие для всех модов проекта.
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "_shared", "devbridge"))
import bridgekit  # noqa: E402

# Порт моста можно переопределить переменной окружения: на машине может
# висеть dev-сервер ДРУГОГО проекта на дефолтном 9878 (например, старой копии
# industrialreforged), и тесты молча уйдут туда, проверяя не тот код.
#   IRBRIDGE_PORT=9879 python3 tools/tests_tank.py
BASE = "http://127.0.0.1:" + os.environ.get("IRBRIDGE_PORT", "9878")
bridgekit.configure(BASE)

# Общие имена из bridgekit: тесты и раннер обращаются к ним через этот модуль.
BridgeDown = bridgekit.BridgeDown
FAILS = bridgekit.FAILS
SKIPS = bridgekit.SKIPS
LAST = bridgekit.LAST
call = bridgekit.call
check = bridgekit.check
skip = bridgekit.skip
summary = bridgekit.summary
take_pending_fails = bridgekit.take_pending_fails
_request = bridgekit.request

DEFAULT_TIMEOUT = bridgekit.DEFAULT_TIMEOUT
# Короткий таймаут для диагностики: /status должен ответить почти мгновенно.
PROBE_TIMEOUT = bridgekit.PROBE_TIMEOUT
# Сколько раз подряд переспрашиваем сервер, прежде чем признать его мёртвым.
#
# Машина бывает загружена (load average заметно выше числа ядер), и тогда один
# жестокий таймаут даёт ложное «сервер умер». Наблюдение: набор с паузой на 80
# тиков не уложился в 15 с, alive() с таймаутом 5 с тоже не успел — и набор
# объявлялся мёртвым, хотя следующие 13 наборов подряд прошли нормально. Сервер
# был жив, просто не успевал. Поэтому смерть проверяем несколько раз подряд.
PROBE_ATTEMPTS = 4
# Один срез ожидания тиков. Держим заведомо меньше общего бюджета, чтобы
# умерший сервер был замечен за WAIT_SLICE, а не за max(60, ticks) секунд, но
# с запасом на медленный тик.
WAIT_SLICE = 90.0


def alive(timeout=PROBE_TIMEOUT):
    """Жив ли сервер. Возвращает (bool, диагностика) — диагностика годна для лога."""
    body, error = _request("/status", None, timeout)
    if error is not None:
        return False, f"мост не отвечает за {timeout:g} с ({error})"
    if not body.get("server", False):
        return False, (
            f"мост отвечает, но сервер не запущен (dist={body.get('dist')}, "
            f"uptime={int(body.get('uptimeMs', 0) / 1000)} с)"
        )
    # /status не отдаёт TPS (DevBridge его не считает), поэтому только то,
    # что сервер реально сообщает: tick, frozen/paused и аптайм.
    return True, (
        f"tick={body.get('tick')} frozen={body.get('frozen')} "
        f"paused={body.get('paused')} игроков={body.get('players')} "
        f"uptime={int(body.get('uptimeMs', 0) / 1000)} с"
    )


def require_alive(where=""):
    """Бросить BridgeDown с понятным текстом, если сервер не отвечает."""
    ok, info = alive()
    if not ok:
        prefix = f"{where}: " if where else ""
        raise BridgeDown(prefix + info + f" ({BASE})")
    return info


def _status_tick():
    """Текущий серверный tick. Бросает BridgeDown, если сервер не отвечает."""
    body, error = _request("/status", None, PROBE_TIMEOUT)
    if error is not None or not body.get("server", False):
        raise BridgeDown("сервер не отвечает на /status — прогон продолжать бессмысленно")
    return int(body.get("tick", 0))


def _status_tick_safe():
    """tick для диагностики; при смерти сервера возвращает -1, не бросает."""
    body, error = _request("/status", None, PROBE_TIMEOUT)
    if error is not None or not body.get("server", False):
        return -1
    return int(body.get("tick", 0))


def execs(*commands, timeout=DEFAULT_TIMEOUT):
    return call("/exec", {"commands": list(commands)}, timeout=timeout)


def wait(ticks, budget=None):
    """Ждёт ticks тиков игры, не умирая молча на зависшем сервере.

    budget передаётся, только если он НЕ стандартный: пауза между наборами в
    раннере намеренно короткая и не должна висеть минуту на зависшем сервере.

    Общий бюджет по умолчанию тот же, что был (секунда на тик), но ожидание идёт
    срезами по WAIT_SLICE секунд: успех отмечается по серверному tick, а если
    мост перестал отвечать — падает с BridgeDown, а не висит до конца бюджета.
    """
    if ticks <= 0:
        return call("/wait", {"ticks": 0}, timeout=PROBE_TIMEOUT)

    # Общий бюджет — тот же, что был (секунда на тик), но ждём срезами по
    # WAIT_SLICE секунд. Мост на каждый срез отвечает либо успехом, либо
    # «timeout waiting N ticks», поэтому прогресс всегда известен по /status.
    budget = float(budget) if budget is not None else max(60.0, float(ticks))
    deadline = time.monotonic() + budget

    ok, info = alive()
    if not ok:
        raise BridgeDown(f"wait({ticks}): {info} ({BASE})")
    target = _status_tick() + ticks

    while True:
        left = deadline - time.monotonic()
        if left <= 0:
            raise BridgeDown(
                f"wait({ticks}): за {budget:g} с сервер не догнал {ticks} тиков "
                f"(осталось {max(0, target - _status_tick_safe())}) — "
                "тики идут медленнее 20/с или сервер завис"
            )

        remaining = max(1, target - _status_tick_safe())
        body, error = _request("/wait", {"ticks": remaining}, timeout=min(WAIT_SLICE, left))
        if error is not None:
            # Срез не уложился. Это ещё не смерть сервера: на загруженной машине
            # он просто не успел, а мост тем временем занят предыдущим запросом.
            # Опрашиваем сервер несколько раз подряд; если он жив — продолжаем
            # ждать следующим срезом, если нет — объявляем смерть.
            info = error
            for attempt in range(1, PROBE_ATTEMPTS + 1):
                ok, info = alive()
                if ok:
                    break
                if attempt < PROBE_ATTEMPTS:
                    print(f"    (wait: сервер не ответил сразу, проба {attempt}/{PROBE_ATTEMPTS} — {info})")
            if not ok:
                raise BridgeDown(f"wait({ticks}) прерван: {info} ({BASE})")
            # Сервер жив, просто не успел за срез — цикл уйдёт в следующий.
            continue

        if body.get("ok") is True and int(body.get("tick", 0)) >= target:
            return body

        # Сервер ответил «timeout waiting N ticks»: тики идут медленнее среза,
        # ждём следующим срезом, бюджет остаётся общим.


def block(pos):
    return call(f"/block?x={pos[0]}&y={pos[1]}&z={pos[2]}")


def be(pos):
    return call(f"/be?x={pos[0]}&y={pos[1]}&z={pos[2]}")


def be_field(pos, name):
    body = be(pos)
    entry = body.get("fields", {}).get(name, {})
    if isinstance(entry, dict) and "value" in entry:
        return entry["value"]
    return entry


def be_set(pos, **params):
    payload = {"x": pos[0], "y": pos[1], "z": pos[2]}
    payload.update(params)
    return call("/be", payload)


def energy(pos):
    return call(f"/energy?x={pos[0]}&y={pos[1]}&z={pos[2]}")


def energy_set(pos, value):
    return call("/energy", {"x": pos[0], "y": pos[1], "z": pos[2], "set": value})


def fluids(pos):
    return call(f"/fluids?x={pos[0]}&y={pos[1]}&z={pos[2]}")


def fluid_fill(pos, fluid, amount):
    return call("/fluids", {"x": pos[0], "y": pos[1], "z": pos[2], "fluid": fluid, "amount": amount})


def items(pos):
    return call(f"/items?x={pos[0]}&y={pos[1]}&z={pos[2]}")


def drops(pos, item=None, count=1):
    """Дроп блока тем же путём, что и при ломании (можно указать инструмент)."""
    query = f"/drops?x={pos[0]}&y={pos[1]}&z={pos[2]}"
    if item:
        query += f"&item={item}&count={count}"
    return call(query)


def dist():
    """CLIENT или SERVER — тесты с реальным игроком работают только на клиенте."""
    return call("/status").get("dist", "SERVER")


def break_block(pos, player="fake"):
    """Ломает блок как игрок (gameMode.destroyBlock) — настоящий путь поломки."""
    return call("/break", {"x": pos[0], "y": pos[1], "z": pos[2], "player": player})


def setblock(pos, block_id, properties=""):
    suffix = f"[{properties}]" if properties else ""
    return execs(f"setblock {pos[0]} {pos[1]} {pos[2]} {block_id}{suffix} replace")


def place(pos, block_id, properties=""):
    """Герметичная установка: сначала air (убивает старый BE), потом блок.

    setblock того же блока сохраняет старый block entity со всем NBT —
    для чистых тестов всегда ставить через place().
    """
    execs(f"setblock {pos[0]} {pos[1]} {pos[2]} minecraft:air")
    wait(2)
    return setblock(pos, block_id, properties)


def clear_container(pos, field, size):
    for slot in range(size):
        be_set(pos, setField=field, slot=slot, item="minecraft:air", count=0)


# ----------------------------------------------------------------------
# Площадки: всё, что нужно раннеру для изоляции наборов
# ----------------------------------------------------------------------
#
# Наборы не снимают форсированную загрузку и не убирают за собой блоки, поэтому
# чанки копятся, а машины продолжают тикать уже после конца своего набора.
# Описание площадок живёт в tests_all.py (там же список наборов), здесь —
# только операции над миром.

class Area:
    """Прямоугольник площадки в оверворлде (и, при необходимости, в Нижнем мире)."""

    def __init__(self, name, x1, z1, x2, z2, y1=None, y2=None, nether=None):
        self.name = name
        self.x1, self.z1, self.x2, self.z2 = x1, z1, x2, z2
        self.y1, self.y2 = y1, y2
        self.nether = nether  # (x1, z1, x2, z2) в minecraft:the_nether

    def forceload_add(self):
        return f"forceload add {self.x1} {self.z1} {self.x2} {self.z2}"

    def forceload_remove(self):
        return f"forceload remove {self.x1} {self.z1} {self.x2} {self.z2}"

    def nether_forceload_remove(self):
        if not self.nether:
            return None
        x1, z1, x2, z2 = self.nether
        return f"execute in minecraft:the_nether run forceload remove {x1} {z1} {x2} {z2}"


def forceload_remove_all():
    """Снимает ВСЮ форсированную загрузку оверворлда и Нижнего мира.

    Дешёвая идемпотентная операция: `/forceload remove all` работает и когда
    ничего не загружено. Вызывается перед каждым набором, чтобы чанки прошлых
    прогонов не тикали.
    """
    execs("forceload remove all",
          "execute in minecraft:the_nether run forceload remove all")


# `/fill` в ванилле отказывает на объёме больше 32768 блоков
# ("commands.fill.tooManyBlocks"), поэтому большие объёмы режем по слоям.
FILL_LIMIT = 30000


def _fill_chunks(x1, y1, z1, x2, y2, z2, block="minecraft:air"):
    """Отдаёт список команд fill, каждая не больше FILL_LIMIT блоков."""
    commands = []
    layer = (x2 - x1 + 1) * (z2 - z1 + 1)
    if layer <= 0:
        return commands
    step = max(1, FILL_LIMIT // layer)
    for y_start in range(y1, y2 + 1, step):
        y_end = min(y2, y_start + step - 1)
        commands.append(f"fill {x1} {y_start} {z1} {x2} {y_end} {z2} {block}")
    return commands


def clear_volume(x1, y1, z1, x2, y2, z2, block="minecraft:air", timeout=DEFAULT_TIMEOUT):
    """Чистит объём блоками (fill), чтобы убить оставшиеся block entity."""
    return execs(*_fill_chunks(x1, y1, z1, x2, y2, z2, block), timeout=timeout)


def clear_box(box, block="minecraft:air", timeout=DEFAULT_TIMEOUT):
    """Чистит один бокс (x1, y1, z1, x2, y2, z2), режа на куски по FILL_LIMIT."""
    return execs(*_fill_chunks(*box, block), timeout=timeout)


def kill_items(x1, y1, z1, x2, y2, z2, timeout=DEFAULT_TIMEOUT):
    """Убивает выпавшие предметы в объёме (тесты роняют блоки через /break)."""
    return execs(
        f"execute if loaded {x1} {y1} {z1} run "
        f"kill @e[type=item,x={x1},y={y1},z={z1},dx={x2 - x1},dy={y2 - y1},dz={z2 - z1}]",
        timeout=timeout,
    )
