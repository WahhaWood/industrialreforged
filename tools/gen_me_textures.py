#!/usr/bin/env python3
"""Draws the GUI backgrounds of the ME machines in the style of screen/filter.png.

Run from anywhere: python3 tools/gen_me_textures.py
Writes textures/screen/me_machine.png, me_recycler.png and me_matter_generator.png (256x256 each).
Coordinates here must match MEMachineScreen / MERecyclerScreen and their menus.
Also paints the ME recycler model texture: the ME macerator one with its blue
and violet tones turned rusty, so the "eater" is told apart at a glance.
"""
import colorsys
from pathlib import Path

from PIL import Image

ASSETS = Path(__file__).resolve().parent.parent / "src/main/resources/assets/industrialreforged/textures"
OUT = ASSETS / "screen"

BLACK = (0, 0, 0, 255)
WHITE = (255, 255, 255, 255)
SHADOW = (0x55, 0x55, 0x55, 255)
PANEL = (0xC6, 0xC6, 0xC6, 255)
SLOT_DARK = (0x37, 0x37, 0x37, 255)
SLOT_IN = (0x8B, 0x8B, 0x8B, 255)


def panel(im, w, h):
    px = im.load()
    for y in range(h):
        for x in range(w):
            px[x, y] = PANEL
    for x in range(w):
        px[x, 0] = BLACK
        px[x, h - 1] = BLACK
    for y in range(h):
        px[0, y] = BLACK
        px[w - 1, y] = BLACK
    for x in range(1, w - 1):
        for y in (1, 2):
            px[x, y] = WHITE
        for y in (h - 3, h - 2):
            px[x, y] = SHADOW
    for y in range(1, h - 1):
        for x in (1, 2):
            if y < h - 3:
                px[x, y] = WHITE
        for x in (w - 3, w - 2):
            if y > 2:
                px[x, y] = SHADOW


def slot(im, x, y):
    """18x18 slot frame with its top-left corner at (x, y); the item sits at (x + 1, y + 1)."""
    px = im.load()
    for i in range(18):
        for j in range(18):
            px[x + i, y + j] = SLOT_IN
    for i in range(18):
        px[x + i, y] = SLOT_DARK
        px[x, y + i] = SLOT_DARK
        px[x + i, y + 17] = WHITE
        px[x + 17, y + i] = WHITE
    px[x + 17, y] = SLOT_IN
    px[x, y + 17] = SLOT_IN


def grid(im, x, y, cols, rows):
    for r in range(rows):
        for c in range(cols):
            slot(im, x + c * 18, y + r * 18)


# 16x16 tab icons: O outline, X fill, . transparent
CHECK = [
    "................",
    "................",
    "................",
    "............OO..",
    "...........OXXO.",
    "..........OXXO..",
    ".........OXXO...",
    "..OO....OXXO....",
    ".OXXO..OXXO.....",
    "..OXXOOXXO......",
    "...OXXXXO.......",
    "....OXXO........",
    ".....OO.........",
    "................",
    "................",
    "................",
]
CROSS = [
    "................",
    "................",
    "..OO........OO..",
    ".OXXO......OXXO.",
    "..OXXO....OXXO..",
    "...OXXO..OXXO...",
    "....OXXOOXXO....",
    ".....OXXXXO.....",
    ".....OXXXXO.....",
    "....OXXOOXXO....",
    "...OXXO..OXXO...",
    "..OXXO....OXXO..",
    ".OXXO......OXXO.",
    "..OO........OO..",
    "................",
    "................",
]


def icon(im, x, y, rows, outline, fill):
    px = im.load()
    for j, row in enumerate(rows):
        for i, ch in enumerate(row):
            if ch == "O":
                px[x + i, y + j] = outline
            elif ch == "X":
                px[x + i, y + j] = fill


def me_machine():
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    panel(im, 176, 145)  # title y 6, search box y 17..29 (EditBox), page row y 124..138 (buttons)
    grid(im, 7, 31, 9, 5)  # recipes: items at (8 + 18c, 32 + 18r)
    icon(im, 200, 0, CHECK, (0x1B, 0x5E, 0x20, 255), (0x55, 0xFF, 0x55, 255))  # "all on" tab
    icon(im, 216, 0, CROSS, (0x7F, 0x00, 0x00, 255), (0xFF, 0x55, 0x55, 255))  # "all off" tab
    im.save(OUT / "me_machine.png")


def me_recycler():
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    panel(im, 176, 150)  # a 9x2 chest: title y 6, inventory label y 56
    grid(im, 7, 17, 9, 2)  # samples: items at (8 + 18c, 18 + 18r)
    grid(im, 7, 66, 9, 3)  # inventory: items at (8 + 18c, 67 + 18r)
    grid(im, 7, 124, 9, 1)  # hotbar: items at (8 + 18c, 125)
    im.save(OUT / "me_recycler.png")


def me_recycler_model():
    src = Image.open(ASSETS / "block/me/me_macerator.png").convert("RGBA")
    px = src.load()
    for y in range(src.height):
        for x in range(src.width):
            r, g, b, a = px[x, y]
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            if a == 0 or s < 0.12:
                continue  # greys and the frame stay as they are
            # blue/violet (h ~ 0.65..0.75) -> rust (h ~ 0.07), a bit more saturated
            h = (h + 0.42) % 1.0
            r2, g2, b2 = colorsys.hsv_to_rgb(h, min(1.0, s * 1.3), v)
            px[x, y] = (round(r2 * 255), round(g2 * 255), round(b2 * 255), a)
    src.save(ASSETS / "block/me/me_recycler.png")


def me_replicator_model():
    """The ME macerator texture with its blue and violet tones turned magenta; greys and the frame stay."""
    src = Image.open(ASSETS / "block/me/me_macerator.png").convert("RGBA")
    px = src.load()
    for y in range(src.height):
        for x in range(src.width):
            r, g, b, a = px[x, y]
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            if a == 0 or s < 0.12:
                continue
            h = (h + 0.2) % 1.0
            r2, g2, b2 = colorsys.hsv_to_rgb(h, min(1.0, s * 1.1), v)
            px[x, y] = (round(r2 * 255), round(g2 * 255), round(b2 * 255), a)
    src.save(ASSETS / "block/me/me_replicator.png")


def me_matter_generator():
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    panel(im, 176, 78)  # title y 6, four status rows at y 20, 32, 44, 56 (MEMatterGeneratorScreen)
    im.save(OUT / "me_matter_generator.png")


def me_matter_generator_model():
    """The ME macerator texture with its blue and violet tones turned green (UU matter), greys and the frame stay."""
    src = Image.open(ASSETS / "block/me/me_macerator.png").convert("RGBA")
    px = src.load()
    for y in range(src.height):
        for x in range(src.width):
            r, g, b, a = px[x, y]
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            if a == 0 or s < 0.12:
                continue
            h = (h - 0.25) % 1.0  # blue/violet (h ~ 0.65..0.75) -> green (h ~ 0.4..0.5)
            r2, g2, b2 = colorsys.hsv_to_rgb(h, min(1.0, s * 1.1), v)
            px[x, y] = (round(r2 * 255), round(g2 * 255), round(b2 * 255), a)
    src.save(ASSETS / "block/me/me_matter_generator.png")


if __name__ == "__main__":
    me_machine()
    me_recycler()
    me_recycler_model()
    me_replicator_model()  # the GUI reuses me_recycler.png (same layout)
    me_matter_generator()
    me_matter_generator_model()
    print("written:", OUT / "me_machine.png", OUT / "me_recycler.png", ASSETS / "block/me/me_recycler.png")
