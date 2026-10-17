#!/usr/bin/env python3
"""Проверка симметрии NBT у всех блок-сущностей.

Зачем. Две самые дорогие ошибки этого класса уже случились: ключ `fuel` делился между
контейнером топлива и счётчиком горения (угол исчезал при каждом сохранении), и ключ
`kUBuffer` писался с одним регистром, а читался с другим (буфер обнулялся). Обе выглядят
как «ничего не происходит», и обе прошли бы любой тест, кроме проверки самих ключей.

Что делает скрипт: у каждого block entity сравнивает набор строковых ключей, которые
читает loadAdditional, с набором, которые пишет saveAdditional, и отдельно сравнивает
getUpdateTag / handleUpdateTag (данные для клиента). Ключи в get/setUpdаte — отдельный слой:
там читать нечего, но писать без чтения бессмысленно.

Что считается ошибкой (FAIL):
  - ключ читается при загрузке, но нигде не пишется при сохранении (поле всегда 0);
  - ключ пишется в getUpdateTag (клиенту), но не читается в handleUpdateTag;
  - один и тот же ключ пишется дважды с разными типами (int и список — как с `fuel`).

Что не ошибка: ключ только пишется (обычно нормально — поле может вычисляться),
ключ только читается, но написан в родительском классе (подсвечивается отдельно).

Использование:
  python3 tools/check_nbt.py            # код 1 при ошибках
  python3 tools/check_nbt.py --verbose  # показать и подозрительные, не только ошибки
"""

import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src/main/java/com/industrialreforged")

READ_NBT = r'\.(get(?:Int|Long|Float|Double|Byte|Short|String|Compound|List|IntArray|LongArray|Boolean)|contains)\(\s*"([^"]+)"'
WRITE_NBT = r'\.(put(?:Int|Long|Float|Double|Byte|Short|String|Compound|List|IntArray|LongArray|Boolean)|put)\(\s*"([^"]+)"'

METHOD_BODY = re.compile(
    r'\b(?P<name>loadAdditional|saveAdditional|getUpdateTag|handleUpdateTag|loadCustomData|saveCustomData)\s*\([^)]*\)\s*\{',
    re.M)

# Осознанные исключения: ключ читается в самом блоке, но пишется не им.
# Пустой список означает «объяснения ещё нет» — тогда это FAIL, а не тихий пропуск.
KNOWN_ONE_WAY = {
    ("IronFurnaceBlockEntity.java", "fuel"):
        "ключ контейнера топлива (SimpleFuelContainer пишет его сам); чтение в BE — миграция со старого счётчика",
    ("SolidHeatGeneratorBlockEntity.java", "fuel"):
        "ключ контейнера топлива (SimpleFuelContainer пишет его сам)",
    ("TankValveBlockEntity.java", "tank"):
        "миграция старого одиночного бака: читается один раз, дальше пишутся waterTank/steamTank",
}


def method_bodies(text):
    """{имя метода: тело} для методов верхнего уровня (с учётом баланса скобок)."""
    bodies = {}
    for match in METHOD_BODY.finditer(text):
        start = match.end()
        depth = 1
        i = start
        while i < len(text) and depth > 0:
            if text[i] == '{':
                depth += 1
            elif text[i] == '}':
                depth -= 1
            elif text[i] == '"' or text[i] == "'":   # пропускаем строки
                quote = text[i]
                i += 1
                while i < len(text) and text[i] != quote:
                    i += 2 if text[i] == '\\' else 1
            i += 1
        bodies.setdefault(match.group("name"), "")
        bodies[match.group("name")] += text[start:i]
    return bodies


def collect_file(path):
    text = open(path, encoding="utf-8").read()
    bodies = method_bodies(text)
    if not bodies:
        return None

    reads, writes, update_writes, update_reads, writes_type = {}, {}, {}, {}, {}
    load = bodies.get("loadAdditional", "") + bodies.get("loadCustomData", "")
    save = bodies.get("saveAdditional", "") + bodies.get("saveCustomData", "")

    for _, key in re.findall(READ_NBT, load):
        reads.setdefault(key, 0)
        reads[key] += 1
    for kind, key in re.findall(WRITE_NBT, save):
        writes.setdefault(key, set()).add(kind.split("(")[0])
    for kind, key in re.findall(WRITE_NBT, bodies.get("getUpdateTag", "")):
        update_writes.setdefault(key, set()).add(kind.split("(")[0])
    for kind, key in re.findall(READ_NBT, bodies.get("handleUpdateTag", "")):
        update_reads.setdefault(key, 0)
        update_reads[key] += 1

    return reads, writes, update_writes, update_reads


def main():
    verbose = "--verbose" in sys.argv
    entities = []
    for dirpath, _, files in os.walk(SRC):
        for name in files:
            if name.endswith("BlockEntity.java") and not name.endswith("AbstractBlockEntity.java"):
                entities.append(os.path.join(dirpath, name))

    entities.sort()
    errors, notes = [], []
    for path in entities:
        data = collect_file(path)
        if data is None:
            continue
        reads, writes, update_writes, update_reads = data
        rel = os.path.relpath(path, SRC)

        # 1) читается при загрузке, но не пишется при сохранении
        for key in reads:
            if key in writes:
                continue
            reason = KNOWN_ONE_WAY.get((os.path.basename(path), key))
            if reason:
                notes.append((rel, f"исключение '{key}': {reason}"))
            else:
                errors.append((rel, f"читается, но не пишется: '{key}' (добавь в KNOWN_ONE_WAY с причиной, если это осознанно)"))
        # 2) один ключ записан разными типами (случай с 'fuel')
        for key, kinds in writes.items():
            if len(kinds) > 1:
                errors.append((rel, f"ключ '{key}' пишется разными типами: {sorted(kinds)}"))
        # 3) шлём клиенту, но не читаем обратно
        for key in update_writes:
            if key not in update_reads:
                errors.append((rel, f"шлём клиенту, но не читаем: '{key}'"))
        # 4) на заметку: пишется, но не читается (обычно норма)
        for key in writes:
            if key not in reads:
                notes.append((rel, f"только пишется: '{key}'"))

    print(f"проверено block entity: {len(entities)}")
    for rel, message in errors:
        print(f"  FAIL {rel}: {message}")
    if verbose:
        for rel, message in notes:
            print(f"  note {rel}: {message}")

    if errors:
        print(f"\nошибок: {len(errors)}")
        return 1

    print(f"ошибок нет; заметок о ключах «только пишется»: {len(notes)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
