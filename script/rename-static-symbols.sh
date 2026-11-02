set -euo pipefail

if [ $# -ne 2 ]; then
  echo "使用方法：./rename-static-symbols.sh [対象までのPath] [prefix]"
  exit 1
fi

target="$1"
prefix="$2"

sysroot=$(rustc --print sysroot)
host=$(rustc -vV | sed -n 's/^host: //p')
tools="$sysroot/lib/rustlib/$host/bin"

work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

# ライブラリ内で定義されているpublic関数を取得
"$tools/llvm-nm" --defined-only --extern-only -j "$target" | tr -d '\r' | grep -Ev ':$|^$' | sort -u > "$work/defined.txt"

# $旧名 $新名となるように変換表を記述(prefixを含む関数名は除外)
{ grep -Ev "$prefix" "$work/defined.txt" || true; } | awk -v p="$prefix" '{ print $0, p $0 }' > "$work/table.txt"

# 関数名を書き換え
"$tools/llvm-objcopy" "--redefine-syms=$work/table.txt" "$target" "$target"
