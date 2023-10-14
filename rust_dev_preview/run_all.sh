#!/usr/bin/env bash

DIRS=(
  cross_service
  examples
  lambda
  webassembly
)

# Clean Cargo.log
rm rust_dev_preview/**/Cargo.lock

export RUSTFLAGS="-D warnings" ;
export APP_ENVIRONMENT="test"

FAIL=()
for f in ${DIRS[@]} ; do
  "$HOME/.cargo/bin/cargo" fmt --manifest-path rust_dev_preview/$f/Cargo.toml --all --check || FAIL+=("fmt:$f")
  "$HOME/.cargo/bin/cargo" clippy --manifest-path rust_dev_preview/$f/Cargo.toml --all || FAIL+=("clippy:$f")
  "$HOME/.cargo/bin/cargo" test --manifest-path rust_dev_preview/$f/Cargo.toml --all || FAIL+=("test:$f")
done

echo $FAIL;
exit "${#FAIL[@]}"

