#!/usr/bin/env bash
# Тонкая обёртка: общий скрипт лежит в projects/_shared/deploy/install_to_mods.sh.
#   bash tools/install_to_mods.sh          # собрать и поставить
#   MODS_DIR=/path bash tools/install_to_mods.sh
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export PROJECT_DIR="$REPO"
exec bash "$REPO/../_shared/deploy/install_to_mods.sh" "$@"
