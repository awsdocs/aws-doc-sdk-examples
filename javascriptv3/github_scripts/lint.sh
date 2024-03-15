#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Filter out JSON files
filtered_files=()
for file in "$@"; do
  if [[ "$file" != *"javascriptv3/"*.json ]]; then
    filtered_files+=("$file")
  fi
done

# Run linting only on filtered files
npm run --prefix javascriptv3 ci-lint -- "${filtered_files[@]//javascriptv3\/}"
