#!/usr/bin/env bash
# Полный цикл save/load: стенд → сохранение → честный перезапуск сервера → сверка.
#
# Зачем этот скрипт, когда есть просто `irbridge_saveload.py --phase setup|check`:
# прошлый вариант слал серверу команду "stop" и сразу стартовал новый инстанс, не дожидаясь
# освобождения порта. Новый сервер молча не поднимал dev-мост ("Адрес уже используется" в
# логе), а /status отвечал от СТАРОГО процесса — то есть рестарта фактически не было, а
# проверка шла против него же и показывала правдоподобные, но бессмысленные числа.
#
# Теперь: ждём освобождения порта, при необходимости добиваем процесс, и печатаем аптайм
# нового сервера (он должен быть маленьким).
#
# Использование:
#   bash tools/run_saveload.sh                 # порты по умолчанию 9879 / 25599
#   BRIDGE_PORT=9881 GAME_PORT=25601 bash tools/run_saveload.sh

set -uo pipefail
PORT="${BRIDGE_PORT:-9879}"
GAME="${GAME_PORT:-25599}"
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO" || exit 1

port_free() {
  ! curl -s --max-time 2 "http://127.0.0.1:${PORT}/status" > /dev/null 2>&1
}

stop_server() {
  curl -s -m 10 -X POST -H 'Content-Type: application/json' \
    -d '{"commands":["stop"]}' "http://127.0.0.1:${PORT}/exec" > /dev/null 2>&1
  for _ in $(seq 1 20); do
    sleep 3
    port_free && return 0
  done
  echo "--- сервер не остановился командой, убиваем процесс"
  pkill -f "net.neoforged.devlaunch.Main" 2>/dev/null
  for _ in $(seq 1 20); do
    sleep 3
    port_free && return 0
  done
  echo "!!! порт ${PORT} всё ещё занят"
  return 1
}

start_server() {
  JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk}" setsid nohup ./gradlew runServer \
    -PbridgePort="${PORT}" -PgamePort="${GAME}" --console=plain \
    > "/tmp/ir_saveload_${1}.log" 2>&1 < /dev/null &
  for _ in $(seq 1 60); do
    sleep 5
    if curl -s --max-time 3 "http://127.0.0.1:${PORT}/status" 2>/dev/null | grep -q '"server":true'; then
      curl -s --max-time 3 "http://127.0.0.1:${PORT}/status" | head -c 80
      echo
      return 0
    fi
  done
  echo "!!! сервер не поднялся (${1}); смотри /tmp/ir_saveload_${1}.log"
  return 1
}

echo "########## 1. сервер до стенда ##########"
port_free || stop_server || exit 1
start_server before || exit 1

echo "########## 2. setup ##########"
python3 tools/irbridge_saveload.py --phase setup --base "http://127.0.0.1:${PORT}" | tail -6

echo "########## 3. остановка сервера (миры сохраняются) ##########"
stop_server || exit 1
sleep 5

echo "########## 4. сервер после рестарта (аптайм должен быть маленьким) ##########"
start_server after || exit 1

echo "########## 5. check ##########"
python3 tools/irbridge_saveload.py --phase check --base "http://127.0.0.1:${PORT}" | tail -20
