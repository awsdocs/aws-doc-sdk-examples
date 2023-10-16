#!/usr/bin/env bash

cd "$(dirname "$0")"

DIRS=(
  cross_service
  examples
  lambda
  webassembly
)

ACTIONS=(
  "fmt --check"
  clippy
  test
)

# Clean Cargo.log
rm **/Cargo.lock

export RUSTFLAGS="-D warnings" ;
export APP_ENVIRONMENT="test"

CARGO="$HOME/.cargo/bin/cargo"

FAIL=()
for f in "${DIRS[@]}" ; do
  for a in "${ACTIONS[@]}" ; do
    "$CARGO" $a --manifest-path $f/Cargo.toml --all || FAIL+=("${a}:$f")
  done
done

echo ${FAIL[@]}
exit ${#FAIL[@]}

