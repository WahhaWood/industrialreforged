#!/usr/bin/env bash
# Thin wrapper around tools/irbridge.py (see that file for the full docs).
exec python3 "$(dirname "$0")/irbridge.py" "$@"
