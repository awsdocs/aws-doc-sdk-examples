#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Recursive function to navigate sub-directories
run_mvn_tests() {
  for dir in "$1"/*/; do
    if [[ -f "$dir/pom.xml" ]]; then
      echo "Running mvn test in $dir"
      (cd "$dir" && mvn test -Dgroups=weathertop -DexcludedGroups=quarantine)
    fi
    if [[ -d "$dir" ]]; then
      run_mvn_tests "$dir"  # Recursively call function for sub-directories
    fi
  done
}

# Root directory
root_dir="example_code"

cd javav2

# Error if the root directory does not exist
if [[ ! -d "$root_dir" ]]; then
  echo "Root directory $root_dir does not exist!"
  exit 1
fi

echo "Starting mvn tests..."
run_mvn_tests "$root_dir"
echo "mvn tests completed."
