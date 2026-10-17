#!/usr/bin/env bash
# Тонкая обёртка: общий скрипт лежит в projects/_shared/devserver/run_dev_server.sh.
# Использование то же: tools/run_dev_server.sh [порт_моста] [порт_игры]
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export PROJECT_DIR="$REPO"
exec bash "$REPO/../_shared/devserver/run_dev_server.sh" "$@"
