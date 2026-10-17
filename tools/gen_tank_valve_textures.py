#!/usr/bin/env python3
"""Текстуры и модели клапана резервуара Industrial Reforged.

Текстуры берутся готовыми
-------------------------
Картинки клапана рисуются вручную и лежат в SOURCE_DIR. Это порты
`accessport_solid_in/out` из Extreme Reactors, перекрашенные под стенку
резервуара:

    синий   — вода   (out = выход воды)
    оранж.  — пар    (out = выход пара)
    _active — клапан работает

Скрипт ничего не рисует: он раскладывает готовые PNG по именам блока и
генерирует модели с blockstate. Если текстуры надо поменять, правь их в
$IR_TEXTURE_SOURCE и запускай скрипт заново.

Модели
------
У блока есть свойство FACING: порт должен смотреть на игрока при установке,
поэтому блочная модель рисует порт только на одной грани, а остальные пять —
стенкой резервуара. Четыре боковых направления получаются поворотом по Y,
а верх и низ — отдельными моделями: поворот по X наложил бы грань up на
грань north, и две грани оказались бы в одной плоскости.

Запуск из корня репозитория:
    python3 tools/gen_tank_valve_textures.py [--source $IR_TEXTURE_SOURCE]
"""

from __future__ import annotations

import argparse
import os
import json
import sys
from pathlib import Path

from PIL import Image

REPO = Path(__file__).resolve().parent.parent
ASSETS = REPO / "src/main/resources/assets/industrialreforged"
TEX_DIR = ASSETS / "textures/block/multiblock"
MODEL_DIR = ASSETS / "models/block/multiblock"
BS_DIR = ASSETS / "blockstates"
ITEM_DIR = ASSETS / "models/item"

SOURCE_DIR = Path(os.environ.get("IR_TEXTURE_SOURCE", "textures-src"))
NS = "industrialreforged"

# Порядок обхода enum в TankValveBlock: вход воды -> выход воды -> вход пара ->
# выход пара. Держим список синхронным с Java, от него зависит и превью.
MODES = ["water_in", "water_out", "steam_in", "steam_out"]

# Режим -> файл на диске. Ключ — (направление, активность, среда).
SOURCES = {
    ("in", False, "water"): "accessport_solid_in_blue.png",
    ("in", True, "water"): "accessport_solid_in_active_blue.png",
    ("out", False, "water"): "accessport_solid_out_blue.png",
    ("out", True, "water"): "accessport_solid_out_active_blue.png",
    ("in", False, "steam"): "accessport_solid_in_orange.png",
    ("in", True, "steam"): "accessport_solid_in_active_orange.png",
    ("out", False, "steam"): "accessport_solid_out_orange.png",
    ("out", True, "steam"): "accessport_solid_out_active_orange.png",
}


def texture_name(mode: str, active: bool) -> str:
    return f"tank_valve_{mode}" + ("_active" if active else "")


def copy_textures(source: Path) -> list[str]:
    """Раскладывает готовые PNG по именам блока, проверяя размер и непрозрачность."""
    if not source.is_dir():
        print(f"нет каталога с текстурами: {source}", file=sys.stderr)
        raise SystemExit(1)

    written: list[str] = []
    for mode in MODES:
        direction = "in" if mode.endswith("_in") else "out"
        fluid = "steam" if mode.startswith("steam") else "water"
        for active in (False, True):
            name = SOURCES[(direction, active, fluid)]
            src = source / name
            if not src.exists():
                print(f"нет исходника: {src}", file=sys.stderr)
                raise SystemExit(1)
            im = Image.open(src).convert("RGBA")
            if im.size != (16, 16):
                print(f"{name}: размер {im.size}, нужен 16x16", file=sys.stderr)
                raise SystemExit(1)
            alpha = im.getchannel("A")
            if min(alpha.get_flattened_data()) != 255:
                print(f"{name}: есть полупрозрачные пиксели — порт станет дырявым",
                      file=sys.stderr)
                raise SystemExit(1)
            im.save(TEX_DIR / f"{texture_name(mode, active)}.png")
            written.append(texture_name(mode, active) + ".png")
    return written


def _face_plates(port_dir: str) -> list[dict]:
    """Шесть граней куба: одна портовая, пять стенчатые.

    Координаты модели: блок занимает [0, 16], ВЕРХ — y=16, НИЗ — y=0,
    север — z=0, юг — z=16, запад — x=0, восток — x=16.

    Грань элемента лежит на СОБСТВЕННОЙ внешней плоскости элемента, поэтому
    каждую грань куба приходится делать плашкой, у которой нужная плоскость
    совпадает с плоскостью блока. Перепутать верх и низ легко: у плашки
    y=0..1 грань `up` окажется на y=1, то есть ВНУТРИ блока, и грань не
    отрендерится — блок будет выглядеть дырявым сверху и снизу.
    """
    plates = [
        ("north", 0, 0, 0, 16, 16, 1),     # грань на z=0
        ("south", 0, 0, 15, 16, 16, 16),   # грань на z=16
        ("up", 0, 15, 0, 16, 16, 16),      # грань на y=16 (верх блока)
        ("down", 0, 0, 0, 16, 1, 16),      # грань на y=0  (низ блока)
        ("west", 0, 0, 0, 1, 16, 16),      # грань на x=0
        ("east", 15, 0, 0, 16, 16, 16),    # грань на x=16
    ]
    return [
        {
            "from": [x0, y0, z0],
            "to": [x1, y1, z1],
            "faces": {d: {"uv": [0, 0, 16, 16],
                          "texture": "#port" if d == port_dir else "#wall"}},
        }
        for d, x0, y0, z0, x1, y1, z1 in plates
    ]


def port_model(name: str, wall: str, port_dir: str) -> str:
    """Модель клапана: порт на одной грани, остальные пять — стенка."""
    return json.dumps({
        "textures": {
            "particle": wall,
            "port": f"{NS}:block/multiblock/{name}",
            "wall": wall,
        },
        "elements": _face_plates(port_dir),
        "ambientocclusion": False,
    }, indent=2, ensure_ascii=False) + "\n"


# Только по вертикали: поворот по X сталкивает грань up с гранью north.
SIDE_ROTATIONS = {
    "facing=north": {},
    "facing=east": {"y": 90},
    "facing=south": {"y": 180},
    "facing=west": {"y": 270},
}


def write_models() -> int:
    """Боковая модель + верх + низ на каждый режим, плюс blockstate."""
    variants: dict[str, dict] = {}
    count = 0

    for active in (False, True):
        wall = f"{NS}:block/multiblock/tank_wall" + ("_active" if active else "")
        for mode in MODES:
            name = texture_name(mode, active)
            active_str = str(active).lower()

            for suffix, port_dir in (("", "north"), ("_top", "up"), ("_bottom", "down")):
                model_name = name + suffix
                (MODEL_DIR / f"{model_name}.json").write_text(
                    port_model(name, wall, port_dir), encoding="utf-8")
                count += 1
                stem = f"{NS}:block/multiblock/{model_name}"

                if not suffix:
                    for facing, rot in SIDE_ROTATIONS.items():
                        key = f"active={active_str},mode={mode},{facing}"
                        variants[key] = {"model": stem} if not rot else {"model": stem, **rot}
                else:
                    facing = "facing=up" if port_dir == "up" else "facing=down"
                    variants[f"active={active_str},mode={mode},{facing}"] = {"model": stem}

    (BS_DIR / "tank_valve.json").write_text(
        json.dumps({"variants": variants}, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8")
    return count + 1


def write_item_model() -> None:
    """Предмет — плоская иконка-лицо клапана (item/generated).

    Было: наследование блочной модели (куб с портом ровно на одной грани). В мире
    это честно, а в инвентаре — тёмный куб: порт виден только на одной грани,
    остальные пять — тёмные стенки. Теперь в инвентаре/руке лежит плоская иконка
    ровно той грани, что стоит в мире по умолчанию (water_in): те же пиксели, что
    и лицевая текстура блока, отдельная копия в textures/item.
    """
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    (ITEM_DIR / "tank_valve.json").write_text(json.dumps({
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"{NS}:item/tank_valve"},
    }, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def preview(out: Path, scale: int = 12) -> None:
    from PIL import ImageDraw

    images = []
    for mode in MODES:
        for active in (False, True):
            name = texture_name(mode, active)
            images.append((name, Image.open(TEX_DIR / f"{name}.png").convert("RGBA")))

    cols, rows = 4, 2
    cell, pad, lbl = 16 * scale, 6, 15
    sheet = Image.new("RGBA", (cols * (cell + pad) + pad,
                               rows * (cell + pad + lbl) + pad), (28, 28, 32, 255))
    d = ImageDraw.Draw(sheet)
    for i, (name, im) in enumerate(images):
        r, c = divmod(i, cols)
        x, y = pad + c * (cell + pad), pad + r * (cell + pad + lbl)
        sheet.alpha_composite(im.resize((cell, cell), Image.NEAREST), (x, y))
        d.text((x, y + cell + 2), name.replace("tank_valve_", ""), fill=(232, 232, 232, 255))
    out.parent.mkdir(parents=True, exist_ok=True)
    sheet.convert("RGB").save(out)
    print(f"preview: {out}")


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--source", default=str(SOURCE_DIR),
                    help="каталог с готовыми текстурами (по умолчанию $IR_TEXTURE_SOURCE)")
    ap.add_argument("--preview", action="store_true")
    args = ap.parse_args()

    TEX_DIR.mkdir(parents=True, exist_ok=True)
    MODEL_DIR.mkdir(parents=True, exist_ok=True)

    written = copy_textures(Path(args.source))
    print(f"текстуры: {len(written)} -> {TEX_DIR}")
    print(f"модели + blockstate: {write_models()}")
    write_item_model()
    print(f"модель предмета: {(ITEM_DIR / 'tank_valve.json').relative_to(REPO)}")

    if args.preview:
        preview(Path("/tmp/opencode/sheets/valve_final.png"))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
