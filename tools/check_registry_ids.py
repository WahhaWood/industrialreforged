#!/usr/bin/env python3
"""Сторож registry id: ловит удаление и переименование блоков/предметов/жидкостей.

Зачем. Мод в активной разработке, и каждый удалённый или переименованный id
ломает уже сгенерированный мир: блок превращается в воздух, в логе появляется
«unknown registry key», а в уже прегенерированных чанках новый блок не появится
никогда. Поэтому список id хранится в tools/registry_ids.json, и любое расхождение
показывается явно.

Что считается ошибкой (FAIL):
  - id исчез        (удалён или переименован)
  - id сменил тип   (был блоком, стал предметом, или жидкость ушла)

Что не ошибка (warn):
  - новые id        (добавление контента — нормально)

Использование:
  python3 tools/check_registry_ids.py --update   # перезаписать эталон после осознанного изменения
  python3 tools/check_registry_ids.py            # просто проверить (код 1 при расхождениях)
"""

import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src/main/java/com/industrialreforged")
BASELINE = os.path.join(ROOT, "tools/registry_ids.json")

# Файлы реестров: id регистрируется вызовом register("<id>"
REGISTRY_FILES = [
    ("block", os.path.join(SRC, "tech/core/ref/IRBlocks.java")),
    ("block", os.path.join(SRC, "transfer/init/GTBlocks.java")),
    ("item", os.path.join(SRC, "tech/core/ref/IRItems.java")),
    ("item", os.path.join(SRC, "transfer/init/GTItems.java")),
    ("item", os.path.join(SRC, "init/GEItems.java")),
    ("fluid", os.path.join(SRC, "tech/core/ref/IRFluids.java")),
    ("blockentity", os.path.join(SRC, "tech/core/ref/IRBlockEntities.java")),
    ("recipe_type", os.path.join(SRC, "tech/core/ref/IRRecipeTypes.java")),
    ("menu", os.path.join(SRC, "tech/core/ref/IRMenuRegistration.java")),
    # AE2 integration (only with AE2): direct registrations plus the ME machines, which are
    # registered in a loop over MEMachineKind (block + item + block entity each)
    ("ae2", os.path.join(SRC, "tech/core/integration/ae2/AE2Integration.java")),
    ("ae2", os.path.join(SRC, "tech/core/integration/ae2/MEMachineKind.java")),
]

REGISTER = re.compile(r'register\(\s*"([a-z0-9_:.]+)"|registerSimpleItem\(\s*"([a-z0-9_]+)"|registerComponentType\(\s*"([a-z0-9_]+)"')
# enum constants of MEMachineKind: FURNACE(\n "me_furnace", ...
KIND_ID = re.compile(r'^\s+[A-Z_]+\(\s*"(me_[a-z0-9_]+)"', re.M)


def collect():
    """{тип: {id: файлы}} — по всем файлам реестров."""
    out = {}
    missing = []
    for kind, path in REGISTRY_FILES:
        if not os.path.isfile(path):
            missing.append(path)
            continue
        text = open(path, encoding="utf-8").read()
        for match in REGISTER.finditer(text):
            ident = next(group for group in match.groups() if group)
            out.setdefault(kind, {})[ident] = os.path.relpath(path, ROOT)
        for match in KIND_ID.finditer(text):
            out.setdefault(kind, {})[match.group(1)] = os.path.relpath(path, ROOT)

    for path in missing:
        print(f"  ! файл реестра не найден: {path}")
    return out


def load_baseline():
    if not os.path.isfile(BASELINE):
        return None
    with open(BASELINE, encoding="utf-8") as handle:
        return json.load(handle)


def main():
    current = collect()
    total = sum(len(v) for v in current.values())
    print(f"зарегистрировано id: {total}")

    baseline = load_baseline()
    if "--update" in sys.argv:
        with open(BASELINE, "w", encoding="utf-8") as handle:
            json.dump(current, handle, ensure_ascii=False, indent=1, sort_keys=True)
        print(f"эталон записан: {os.path.relpath(BASELINE, ROOT)}")
        return 0

    if baseline is None:
        print(f"эталона нет. Создай его: python3 tools/check_registry_ids.py --update")
        return 1

    # Раскладываем id по наборам реестров: один и тот же id закономерно стоит
    # сразу в blocks и в items (блок и его BlockItem), поэтому сравнивать надо
    # не по отдельному реестру, а по множеству реестров, где id встречается.
    def kinds_map(data):
        out = {}
        for kind, ids in data.items():
            for ident in ids:
                out.setdefault(ident, set()).add(kind)
        return out

    baseline_map = kinds_map(baseline)
    current_map = kinds_map(current)

    removed, retyped, added = [], [], []
    for ident, kinds in baseline_map.items():
        if ident not in current_map:
            removed.append((ident, kinds))
        elif current_map[ident] != kinds:
            retyped.append((ident, kinds, current_map[ident]))
    for ident in current_map:
        if ident not in baseline_map:
            added.append((ident, current_map[ident]))

    for ident, kinds in removed:
        print(f"  FAIL удалён: {'+'.join(sorted(kinds))}:{ident}"
              f"  (мир с этим id поедет — блок станет воздухом)")
    for ident, was, now in retyped:
        print(f"  FAIL смена состава реестров: {ident}: {'+'.join(sorted(was))}"
              f" -> {'+'.join(sorted(now))}")
    print(f"новых id: {len(added)} (это нормально, если это новый контент)")

    if removed or retyped:
        print()
        print("Если изменения осознанные — обнови эталон:")
        print("  python3 tools/check_registry_ids.py --update")
        return 1

    print("расхождений нет: ни один id не удалён и не сменил тип")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
