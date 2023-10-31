#!/usr/bin/env bash -x

cd "$(dirname "$0")"

DIRS=(
  cross_service
  examples
  lambda
  webassembly
)

ACTIONS=(
  "fmt --check --all"
  "clippy --all"
  "test --all"
)

if [ "$1" == "--clean" ] ; then 
  rm **/Cargo.lock
  ACTIONS=("clean" "${ACTIONS[@]}")
fi

export RUSTFLAGS="-D warnings" ;
export APP_ENVIRONMENT="test"

CARGO="$HOME/.cargo/bin/cargo"

FAIL=()
for f in "${DIRS[@]}" ; do
  for a in "${ACTIONS[@]}" ; do
    "$CARGO" $a --manifest-path $f/Cargo.toml || FAIL+=("${a}:$f")
  done
done

echo "${FAIL[@]}"
exit ${#FAIL[@]}

