#!/usr/bin/env python3
"""Генерация плейсхолдер-текстур тепловых труб и охладителей (16x16).

Плейсхолдеры: нормальный металлический вид, чтобы не стыдно было в игре.
Заменить на рисованные можно, положив PNG с теми же именами.
"""

from PIL import Image
import os

OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                   "src/main/resources/assets/industrialreforged/textures/block/heat")

METAL = (154, 154, 158, 255)
METAL_LIGHT = (182, 182, 186, 255)
METAL_DARK = (110, 110, 114, 255)
BORDER = (72, 72, 76, 255)
HOLE = (38, 38, 40, 255)
VENT_DARK = (52, 52, 54, 255)
VENT_LIGHT = (208, 208, 210, 255)

TIERS = {
    "heat_pipe_1": (184, 115, 51, 255),    # медь
    "heat_pipe_2": (127, 159, 191, 255),   # сталь-голубец
    "heat_pipe_3": (65, 208, 200, 255),    # циан
}


def base_metal():
    img = Image.new("RGBA", (16, 16), METAL)
    px = img.load()
    for i in range(16):
        px[i, 0] = METAL_LIGHT
        px[0, i] = METAL_LIGHT
        px[i, 15] = METAL_DARK
        px[15, i] = METAL_DARK
        px[i, 0] = BORDER if i in (0, 15) else px[i, 0]
        px[0, i] = BORDER if i in (0, 15) else px[0, i]
    # рамка
    for i in range(16):
        px[i, 0] = BORDER
        px[i, 15] = BORDER
        px[0, i] = BORDER
        px[15, i] = BORDER
    return img, px


def pipe(name, core):
    img, px = base_metal()
    # фланец 10x10 по центру
    for y in range(3, 13):
        for x in range(3, 13):
            px[x, y] = METAL_DARK if (x in (3, 12) or y in (3, 12)) else METAL
    # кольцо из основного цвета
    ring = [(7, 4), (8, 4), (4, 7), (4, 8), (7, 11), (8, 11), (11, 7), (11, 8)]
    for x, y in ring:
        px[x, y] = core
    for x, y in [(6, 5), (7, 5), (8, 5), (9, 5), (6, 10), (7, 10), (8, 10), (9, 10),
                 (5, 6), (5, 7), (5, 8), (5, 9), (10, 6), (10, 7), (10, 8), (10, 9)]:
        px[x, y] = core
    # отверстие
    for y in range(7, 9):
        for x in range(7, 9):
            px[x, y] = HOLE
    # болтики
    for x, y in [(4, 4), (11, 4), (4, 11), (11, 11)]:
        px[x, y] = METAL_LIGHT
    img.save(os.path.join(OUT, name + ".png"))


def main():
    os.makedirs(OUT, exist_ok=True)
    for name, core in TIERS.items():
        pipe(name, core)
    print("done:", OUT)


if __name__ == "__main__":
    main()
