#!/usr/bin/env python3
"""Статическая проверка ассетов: blockstates, модели, текстуры.

Чистый Python, dev-мост не нужен.

Правила бывают двух сортов, и это важно: часть проверок универсальная, часть
прицельная. Универсальные ловят очевидный брак по всему моду, прицельные
следят за контрактом конкретной модели.

Универсальные (на все blockstates)
    - файл читается, ключ варианта в формате свойство=значение
    - модель из варианта существует
    - все `#переменная` в модели объявлены в её textures
    - все текстуры, на которые ссылается модель, физически есть
    - повороты только 0/90/180/270
    - атласные текстуры — квадрат степени двойки

Прицельные (см. CUBE_MODELS)
    - блок действительно куб: все шесть граней лежат на плоскостях блока
    - текстуры полностью непрозрачные
    - ровно одна портовая грань

Прицельные правила нельзя применять ко всем моделям. Например, у
molecular_transformer внутренние грани законны: это многокомпонентная
BlockBench-модель, и они видимы. А атласные текстуры нельзя проверять у OBJ:
модель с `loader: neoforge:obj` грузит их мимо атласа, там произвольный размер
законен.

Usage:
    python3 tools/check_assets.py [--quiet]
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
ASSETS = REPO / "src/main/resources/assets"
MODID = "industrialreforged"
ROOT = ASSETS / MODID

# Блок занимает [0, 16]: верх — y=16, низ — y=0, север — z=0, юг — z=16,
# запад — x=0, восток — x=16.
FACE_PLANE = {
    "north": ("z", 0), "south": ("z", 16),
    "up": ("y", 16), "down": ("y", 0),
    "west": ("x", 0), "east": ("x", 16),
}
AXIS_INDEX = {"x": 0, "y": 1, "z": 2}
FACE_CORNERS = {"north_east", "north_west", "south_east", "south_west"}

# Модели, которые обязаны быть цельным непрозрачным кубом с ОДНОЙ портовой
# гранью. Клапан резервуара собирается из шести плашек-граней, и у плашки
# `y: 0..1` грань `up` оказывается на y=1, то есть внутри блока: грань не
# рисуется, и блок выглядит дырявым сверху и снизу. В JSON это не видно,
# в игре видно сразу.
CUBE_MODELS = ("block/multiblock/tank_valve_",)


class Report:
    def __init__(self) -> None:
        self.problems: list[str] = []
        self.notes: list[str] = []
        self.passed = 0

    def fail(self, where: str, message: str) -> None:
        self.problems.append(f"{where}: {message}")

    def note(self, where: str, message: str) -> None:
        """Замечено, но не ошибка: накопленное в моде, не наша зона ответственности."""
        self.notes.append(f"{where}: {message}")

    def ok(self) -> None:
        self.passed += 1

    @property
    def failed(self) -> int:
        return len(self.problems)


def short(path: Path) -> str:
    try:
        return str(path.relative_to(REPO))
    except ValueError:
        return str(path)


def load_json(path: Path, rep: Report) -> dict | None:
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:  # noqa: BLE001
        rep.fail(short(path), f"нечитаемый JSON: {exc}")
        return None


def is_custom_loader(model: dict) -> bool:
    """Модель с чужим loader (OBJ и подобные) грузит текстуры мимо атласа."""
    return "loader" in model


def resolve_texture(ref: str, textures: dict) -> Path | None:
    """`#var` -> файл текстуры; `ns:path` -> файл; None — чужой namespace."""
    if ref.startswith("#"):
        target = textures.get(ref[1:])
        if not isinstance(target, str):
            return None
        ref = target
    ns, _, path = ref.partition(":")
    if ns != MODID:
        return None
    return ROOT / "textures" / f"{path}.png"


def check_atlas_texture(path: Path, rep: Report, block_id: str = "") -> None:
    """Текстура атласа: должна существовать и быть не меньше 16.

    Не квадрат степени двойки — не поломка: Minecraft такие принимает, просто
    ограничивает уровень мипов. Поэтому это замечание, а не ошибка.
    """
    rel = short(path)
    if not path.exists():
        # Модель незарегистрированного сироты тянет за собой и отсутствующие
        # текстуры: в игре они не загрузятся, чинить бессмысленно.
        if block_id and not block_registered(block_id):
            rep.note(rel, f"сирота: нет текстуры для блока {block_id}")
        else:
            rep.fail(rel, "текстура не найдена")
        return
    try:
        from PIL import Image
    except ImportError:
        return
    w, h = Image.open(path).size
    if min(w, h) < 16:
        rep.fail(rel, f"размер {w}x{h}: меньше 16, текстуру не увеличить")
        return
    if w != h or (w & (w - 1)) != 0:
        rep.note(rel, f"размер {w}x{h} не квадрат степени двойки — мипмапы будут огранены")
        return
    rep.ok()


def check_opaque_texture(path: Path, rep: Report) -> None:
    """Текстура непрозрачная: иначе куб рендерится дырявым."""
    rel = short(path)
    if not path.exists():
        rep.fail(rel, "текстура не найдена")
        return
    try:
        from PIL import Image
    except ImportError:
        return
    alpha = Image.open(path).convert("RGBA").getchannel("A")
    lo = min(alpha.get_flattened_data())
    if lo != 255:
        rep.fail(rel, f"есть полупрозрачные пиксели (alpha={lo}) — куб будет дырявым")
        return
    rep.ok()


def check_cube_contract(model_path: Path, model: dict, rep: Report, textures: set[Path]) -> None:
    """Прицельные правила: блок — цельный куб с ровно одной портовой гранью."""
    rel = short(model_path)
    port_faces: list[str] = []

    for i, element in enumerate(model.get("elements", [])):
        lo, hi = element.get("from"), element.get("to")
        if not lo or not hi:
            rep.fail(rel, f"элемент {i}: нет from/to")
            continue
        for axis in ("x", "y", "z"):
            j = AXIS_INDEX[axis]
            if not 0 <= lo[j] <= hi[j] <= 16:
                rep.fail(rel, f"элемент {i}: {axis}={lo[j]}..{hi[j]} вне [0, 16]")
        for face in element.get("faces", {}):
            if face in FACE_CORNERS:
                continue
            plane = FACE_PLANE.get(face)
            if plane is None:
                continue
            axis, value = plane
            j = AXIS_INDEX[axis]
            edge = lo[j] if value == 0 else hi[j]
            if edge != value:
                rep.fail(rel, f"грань {face} элемента {i} на {axis}={edge}, "
                              f"а куб требует {axis}={value} — грань уйдёт внутрь блока")
            if element["faces"][face].get("texture") == "#port":
                port_faces.append(face)

    if len(port_faces) != 1:
        rep.fail(rel, f"портовых граней {len(port_faces)} ({port_faces}), ожидалась ровно одна")

    for tex in textures:
        check_opaque_texture(tex, rep)


def check_model(model_path: Path, rep: Report, cache: dict,
                 block_id: str = "") -> set[Path]:
    """Проверяет модель. Возвращает множество задействованных текстур."""
    rel = short(model_path)
    model = cache.get(model_path)
    if model is None:
        model = load_json(model_path, rep)
        if model is None:
            return set()
        cache[model_path] = model

    textures: dict = model.get("textures", {})
    used: set[Path] = set()
    custom_loader = is_custom_loader(model)

    for ref in textures.values():
        if not isinstance(ref, str):
            continue
        resolved = resolve_texture(ref, textures)
        if resolved is None:
            continue
        used.add(resolved)
        # OBJ и прочие кастомные loader'ы идут мимо атласа.
        if not custom_loader:
            check_atlas_texture(resolved, rep, block_id)

    for i, element in enumerate(model.get("elements", [])):
        for face, face_def in element.get("faces", {}).items():
            ref = face_def.get("texture")
            if ref is None:
                rep.fail(rel, f"грань {face} элемента {i}: нет texture")
            elif isinstance(ref, str) and ref.startswith("#") and ref[1:] not in textures:
                rep.fail(rel, f"грань {face} элемента {i}: {ref} не объявлена в textures")
            uv = face_def.get("uv")
            if uv and not (0 <= uv[0] <= uv[2] <= 16 and 0 <= uv[1] <= uv[3] <= 16):
                rep.fail(rel, f"грань {face} элемента {i}: uv={uv} вне [0, 16]")


    name = str(model_path)
    if any(p in name for p in CUBE_MODELS):
        check_cube_contract(model_path, model, rep, used)

    rep.ok()
    return used


BLOCKS_JAVA = REPO / "src/main/java/com/industrialreforged/tech/core/ref/IRBlocks.java"
_registered_cache: dict[str, bool] | None = None


def block_registered(block_id: str) -> bool:
    """Заведён ли блок в реестре IRBlocks.register(...)."""
    global _registered_cache
    if _registered_cache is None:
        _registered_cache = {}
        if BLOCKS_JAVA.exists():
            text = BLOCKS_JAVA.read_text(encoding="utf-8")
            for name in re.findall(r'REGISTRY\.register\("([a-z0-9_]+)"', text):
                _registered_cache[name] = True
    return block_id in _registered_cache


def check_blockstate(bs_path: Path, rep: Report, cache: dict) -> None:
    rel = short(bs_path)
    bs = load_json(bs_path, rep)
    if bs is None:
        return
    variants = bs.get("variants")
    if not isinstance(variants, dict):
        if "multipart" in bs:
            # multipart — легитимная альтернатива variants (трубы, заборы).
            rep.ok()
            return
        rep.fail(rel, "нет ни variants, ни multipart")
        return

    # Пустой ключ "" — ванильная запись для блока без свойств состояния.
    allow_empty = len(variants) == 1 and "" in variants

    for key, spec in variants.items():
        if not key:
            if not allow_empty:
                rep.fail(rel, "пустой ключ варианта допустим только для единственного варианта")
        elif "=" not in key.split(",")[0]:
            rep.fail(rel, f"ключ варианта «{key}» не в формате свойство=значение")

        if not isinstance(spec, dict):
            rep.fail(rel, f"вариант «{key}»: ожидался объект, получено {type(spec).__name__}")
            continue

        model_ref = spec.get("model")
        if not isinstance(model_ref, str):
            rep.fail(rel, f"вариант «{key}»: нет строки model "
                          f"(вложенные sub-variants Minecraft 1.21.1 не разворачивает)")
            continue
        for axis in ("x", "y"):
            # Ванилла принимает и отрицательные углы: -90 это 270.
            if axis in spec and int(spec[axis]) % 360 not in (0, 90, 180, 270):
                rep.fail(rel, f"вариант «{key}»: поворот {axis}={spec[axis]} недопустим")

        ns, _, path = model_ref.partition(":")
        if ns != MODID:
            # Ссылка на ванилльскую модель законна: листва и стекло наследуют
            # ванильную форму блока.
            rep.note(rel, f"вариант «{key}»: чужое namespace {model_ref}")
            continue
        model_path = ROOT / "models" / f"{path}.json"
        if not model_path.exists():
            # Модель может быть незарегистрированным сиротой: blockstate лежит,
            # а блок в реестре не заведён. Это не ломает игру, но копится.
            block_id = bs_path.stem
            if block_registered(block_id):
                rep.fail(rel, f"вариант «{key}»: блок {block_id} зарегистрирован, "
                              f"но нет модели {short(model_path)}")
            else:
                rep.note(rel, f"сирота: блок {block_id} не зарегистрирован, "
                              f"модели {short(model_path)} нет")
            continue
        check_model(model_path, rep, cache, bs_path.stem)

    rep.ok()


def check_tank_mode_order(rep: Report) -> None:
    """Порядок обхода клапана продублирован в Java и в генераторе текстур.

    Расхождение не ломает игру, но молча портит раскладку моделей и превью.
    """
    java = REPO / "src/main/java/com/industrialreforged/tech/multiblock/tank/TankValveBlock.java"
    py = REPO / "tools/gen_tank_valve_textures.py"
    if not java.exists() or not py.exists():
        rep.fail("TankValveMode", "не найден Java-блок или Python-генератор")
        return

    jtext = java.read_text(encoding="utf-8")
    body = jtext[jtext.index("enum TankValveMode"):]
    java_names = [name for _, name in
                  re.findall(r'^\s+(\w+)\("(\w+)"\)', body, re.MULTILINE)]

    ptext = py.read_text(encoding="utf-8")
    m = re.search(r"^MODES\s*=\s*\[(.*?)\]", ptext, re.MULTILINE | re.DOTALL)
    if not m:
        rep.fail("gen_tank_valve_textures.py", "не найден список MODES")
        return
    py_names = re.findall(r'"(\w+)"', m.group(1))

    if java_names != py_names:
        rep.fail("TankValveMode",
                 f"порядок режимов разошёлся: Java {java_names} != Python {py_names}")
    else:
        rep.ok()


def main(argv=None) -> int:
    # argv передаётся раннером tests_all.py: тот разбирает свои --base/--timeout,
    # и argparse здесь не должен отвергать чужие флаги.
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--quiet", action="store_true")
    args, _unknown = ap.parse_known_args(argv)

    rep = Report()
    cache: dict = {}

    blockstates = sorted((ROOT / "blockstates").glob("*.json"))
    if not blockstates:
        print("нет blockstates — проверять нечего", file=sys.stderr)
        return 1

    for bs in blockstates:
        check_blockstate(bs, rep, cache)

    check_tank_mode_order(rep)

    for problem in rep.problems:
        print(f"  FAIL {problem}")
    if not args.quiet and rep.notes:
        unique = sorted(set(rep.notes))
        print(f"-- замечания ({len(rep.notes)}, уникальных {len(unique)}): не ошибки, "
              f"накопленное в моде")
        for note in unique[:15]:
            print(f"  note {note}")
        if len(unique) > 15:
            print(f"  ... ещё {len(unique) - 15}")
    tail = " пройдено" if not args.quiet else ""
    print(f"=== check_assets: {rep.failed} провалено, {rep.passed} проверок{tail} ===")
    return 1 if rep.failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
