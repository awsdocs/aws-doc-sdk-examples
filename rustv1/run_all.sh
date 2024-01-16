#!/usr/bin/env bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

cd "$(dirname "$0")" || exit

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
  rm ./**/Cargo.lock
  ACTIONS=("clean" "${ACTIONS[@]}")
fi

export RUSTFLAGS="-D warnings" ;
export APP_ENVIRONMENT="test"

FAIL=()
for f in "${DIRS[@]}" ; do
  # we _do_ want to break out the flags in $a
  # shellcheck disable=SC2086
  for a in "${ACTIONS[@]}" ; do
    cargo $a --manifest-path "$f/Cargo.toml" || FAIL+=("${a}:$f")
  done
done

echo "${FAIL[@]}"
exit ${#FAIL[@]}
