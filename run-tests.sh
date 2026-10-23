#!/usr/bin/env sh
set -eu

# Locate the project root from this script.
SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_ROOT"

if ! command -v javac >/dev/null 2>&1 || \
   ! command -v java >/dev/null 2>&1; then
    echo "ERROR: Java JDK 8 or newer is required." >&2
    exit 2
fi

BUILD_DIR="$SCRIPT_DIR/.build"
mkdir -p "$BUILD_DIR"

trap 'rm -rf "$BUILD_DIR"' EXIT HUP INT TERM

javac -encoding UTF-8 -d "$BUILD_DIR" \
    Spellseek.java "$SCRIPT_DIR/SpellseekTest.java"

java -cp "$BUILD_DIR" SpellseekTest "$SCRIPT_DIR/fixtures"
