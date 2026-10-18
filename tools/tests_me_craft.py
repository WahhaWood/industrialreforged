#!/usr/bin/env python3
"""E2E ME-машины и автокрафт AE2: время расчёта, байты плана, возврат входов при отмене.

Сеть та же, что в bench_me.py (контроллеры, ячейки, CPU 4×256k, ME-дробитель/печь/формовщик/компрессор).

Usage: IRBRIDGE_PORT=9879 python3 tools/tests_me_craft.py [штук_для_расчёта=100000]
"""

import json
import sys
import time

import bench_me as b
import irbridge_testkit as t
from irbridge_testkit import check, execs, wait

ORE, CRUSHED, IRON_DUST = b.ORE, b.CRUSHED, "industrialreforged:iron_dust"
MAC, C1 = b.MAC, b.C1


def timed_craft(item, count):
    started = time.time()
    result = b.ae2("craft", C1, item=item, count=count)
    return result, time.time() - started


def main():
    count = int(sys.argv[1]) if len(sys.argv) > 1 else 100000
    b.build("ae2")
    info = b.ae2("info", C1)
    check("сеть: CPU собран, питание есть", info.get("cpus") == 1 and info.get("powered") is True, str(info))

    # --- расчёт и байты: дроблёное железо делают несколько паттернов, пыль — ещё больше ---
    b.ae2("insert", C1, item=ORE, count=count)
    b.ae2("insert", C1, item="minecraft:deepslate_iron_ore", count=count)
    b.ae2("insert", C1, item="minecraft:raw_iron", count=count)
    wait(10)
    report = {}
    for item in (CRUSHED, IRON_DUST):
        result, seconds = timed_craft(item, count)
        report[item] = {"seconds": round(seconds, 2), "bytes": result.get("bytes"), "result": result.get("ok"),
                        "simulation": result.get("simulation"), "missing": result.get("missing")}
        check(f"расчёт {count} {item.split(':')[1]} принят и быстрее 20 с",
              result.get("ok") is True and seconds < 20, str(report[item]))
        b.ae2("cancel", C1)
        wait(40)

    # --- отмена: невыполненные операции в очереди машины возвращаются в сеть ---
    ore_before = b.stored(ORE)
    crushed_before = b.stored(CRUSHED)
    result, _ = timed_craft(CRUSHED, 16000)
    check("заказ 16000 дроблёного железа принят", result.get("ok") is True, str(result))
    queued = 0
    for _ in range(60):
        queued = b.ae2("info", MAC).get("queued", 0)
        if queued > 0:
            break
        wait(1)
    check("машина получила работу от CPU", queued > 0, f"queued={queued}")
    execs("tick freeze")
    try:
        before_cancel = b.ae2("info", MAC)
        check("до отмены в очереди машины есть работа", before_cancel.get("queued", 0) > 0, str(before_cancel))
        cancelled = b.ae2("cancel", C1)
        after_cancel = b.ae2("info", MAC)
        check("отмена: задание остановлено", cancelled.get("cancelled") == 1, str(cancelled))
        check("отмена: очередь машины пуста сразу, входы ждут возврата в сети",
              after_cancel.get("queued") == 0 and after_cancel.get("pendingOutputs", 0) > 0, str(after_cancel))
    finally:
        execs("tick unfreeze")

    wait(60)
    info = b.ae2("info", MAC)
    ore_after = b.stored(ORE)
    crushed_after = b.stored(CRUSHED) - crushed_before
    check("после отмены машина пуста, буфер вернулся в сеть", info.get("queued") == 0 and info.get("pendingOutputs") == 0, str(info))
    check("после отмены не заказанное не переработано", 0 <= crushed_after < 16000, f"дроблёного {crushed_after}")
    check("руда не потеряна: ушло ровно на выпущенное (2 дроблёного на руду)",
          ore_before - ore_after == crushed_after // 2 and crushed_after % 2 == 0, f"руды {ore_before} -> {ore_after}, дроблёного +{crushed_after}")

    print(json.dumps(report, ensure_ascii=False, indent=1))
    execs(f"fill 705 {b.Y - 2} -585 716 {b.Y + 4} -575 minecraft:air", b.AREA.replace(" add ", " remove "))
    return t.summary("tests_me_craft")


if __name__ == "__main__":
    raise SystemExit(main())
