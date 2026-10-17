#!/usr/bin/env python3
"""Перекрашивает фотоэлементы в цвет верхней части панели своего уровня.

Форма — одна на все уровни (как у ядер): исходная полосатая ячейка лежит
в `tools/photocell_bases/photocell_1_level.png` (там же архив остальных
исходных форм). Для каждого уровня:
  1. у `solar_generator_N_level_top` берём самый частый «освещённый» цвет
     (тёмная рамка отбрасывается) — это цвет панели;
  2. форму фотоэлемента перекрашиваем: оттенок ставим ровно панельный,
     насыщенность подгоняем итеративно; светотень и прозрачность сохраняются.

Требуется ImageMagick (`magick`). Запуск: python3 tools/recolor_photocells.py
"""
import colorsys
import re
import subprocess
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PANEL = str(ROOT / "src/main/resources/assets/industrialreforged/textures/block/generator/electric/solar_generator_{}_level_top.png")
OUT = str(ROOT / "src/main/resources/assets/industrialreforged/textures/item/crafting/photocell_{}_level.png")
BASE = Path(__file__).resolve().parent / "photocell_bases" / "photocell_1_level.png"


def lit_rep(path):
    """Самый частый непрозрачный «освещённый» цвет: (оттенок°, насыщенность%, светлота)."""
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
        pixels.append((r, g, b, h * 360, s * 100, l * 100))
    if not pixels:
        return 0.0, 0.0, 0.0
    (r, g, b), _ = Counter((p[0], p[1], p[2]) for p in pixels).most_common(1)[0]
    h, s, l = [p[3:] for p in pixels if (p[0], p[1], p[2]) == (r, g, b)][0]
    return h, s, l


def build(level, hue, sat_scale, bright_scale):
    subprocess.run(
        [
            "magick", str(BASE),
            "-modulate", f"{max(0.3, min(2.5, bright_scale)) * 100:.0f},{max(0.0, min(2.5, sat_scale)) * 100:.0f},0",
            "-colorspace", "HSL", "-channel", "H",
            "-evaluate", "set", f"{hue / 360 * 100:.4f}%",
            "+channel", "-colorspace", "sRGB", OUT.format(level),
        ],
        check=True,
    )


def main():
    if not BASE.exists():
        raise SystemExit(f"нет базовой формы: {BASE}")
    base_h, base_s, base_l = lit_rep(BASE)
    for level in range(1, 16):
        hue, sat, light = lit_rep(PANEL.format(level))
        bright = light / base_l if base_l > 1 else 1.0
        if sat < 3:  # панель почти серая: фотоэлемент остаётся серым
            build(level, hue, 0.0, bright)
            print(f"{level:>2}: серое (панель без насыщенности)")
            continue

        k = base_s / max(sat, 1.0) if sat > 0 else 1.0
        s2 = base_s
        for _ in range(8):
            build(level, hue, k, bright)
            h2, s2, l2 = lit_rep(OUT.format(level))
            if abs(s2 - sat) <= 3:
                break
            k = max(0.0, min(2.5, k * sat / max(s2, 1.0)))
        print(f"{level:>2}: панель {hue:5.0f}°,{sat:4.0f}%,L{light:4.0f} -> фотоэлемент {s2:4.0f}%,L{l2:4.0f}")


if __name__ == "__main__":
    main()
