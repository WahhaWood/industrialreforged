#!/usr/bin/env python3
"""Перекрашивает текстуры энергоядер в цвет верхней части своей панели.

Для каждого уровня:
  1. у текстуры `solar_generator_N_level_top` берём самый частый «освещённый»
     цвет (чёрная рамка отбрасывается) — это и есть цвет панели;
  2. базовую форму ядра (`tools/core_base.png`) перекрашиваем: насыщенность
     подгоняем под панель (полностью серая панель -> серое ядро), оттенок
     ставим ровно панельный; светотень и прозрачность базы сохраняются.

Требуется ImageMagick (`magick`). Запуск: python3 tools/recolor_cores.py
"""
import colorsys
import re
import subprocess
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PANEL = str(ROOT / "src/main/resources/assets/industrialreforged/textures/block/generator/electric/solar_generator_{}_level_top.png")
OUT = str(ROOT / "src/main/resources/assets/industrialreforged/textures/item/solar_core_{}_level.png")
BASE = Path(__file__).resolve().parent / "core_base.png"


def lit_rep(path):
    """Самый частый непрозрачный «освещённый» цвет: (оттенок°, насыщенность%)."""
    out = subprocess.run(["magick", str(path), "txt:-"], capture_output=True, text=True).stdout
    pixels = []
    for line in out.splitlines()[1:]:
        m = re.match(r"\d+,\d+:\s*\(\s*(\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+))?", line)
        if not m:
            continue
        r, g, b = int(m.group(1)), int(m.group(2)), int(m.group(3))
        a = int(m.group(4)) if m.group(4) else 255
        if a < 128 or max(r, g, b) / 255 < 0.15:
            continue
        h, l, s = colorsys.rgb_to_hls(r / 255, g / 255, b / 255)
        pixels.append((r, g, b, h * 360, s * 100))
    (r, g, b), _ = Counter((p[0], p[1], p[2]) for p in pixels).most_common(1)[0]
    h, s = [p[3:] for p in pixels if (p[0], p[1], p[2]) == (r, g, b)][0]
    return h, s


def build(level, hue, sat_scale):
    subprocess.run(
        [
            "magick", str(BASE),
            "-modulate", f"100,{max(0.0, min(2.0, sat_scale)) * 100:.0f},0",
            "-colorspace", "HSL", "-channel", "H",
            "-evaluate", "set", f"{hue / 360 * 100:.4f}%",
            "+channel", "-colorspace", "sRGB", str(OUT).format(level),
        ],
        check=True,
    )


def main():
    for level in range(1, 16):
        hue, sat = lit_rep(PANEL.format(level))
        if sat < 3:  # почти серый/чёрный: ядро должно остаться серым
            build(level, hue, 0.0)
            print(f"{level:>2}: серое (панель без насыщенности)")
            continue

        k = 1.0
        for _ in range(5):
            build(level, hue, k)
            h2, s2 = lit_rep(OUT.format(level))
            if abs(s2 - sat) <= 4:
                break
            k *= sat / max(s2, 1.0)
        print(f"{level:>2}: оттенок {hue:5.0f}°, насыщенность {sat:4.0f}% -> {s2:4.0f}%")


if __name__ == "__main__":
    main()
