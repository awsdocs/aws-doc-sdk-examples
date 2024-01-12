#!/usr/bin/env bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

cd "$(dirname "$0")" || exit

mkdir -p vendors
cd vendors || exit

# Find more languages at https://tree-sitter.github.io/tree-sitter/#parsers
LANGUAGES=(
  "tree-sitter/tree-sitter-python"
  "tree-sitter/tree-sitter-javascript"
  "tree-sitter/tree-sitter-rust"
)

for L in "${LANGUAGES[@]}" ; do 
  D=$(basename "$L")
  if [ -d "$D" ] ; then
    (
      cd "$(basename "$D")" || exit
      git pull
    )
  else
    git clone "git@github.com:$L.git"
  fi
done