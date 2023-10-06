#!/bin/bash

# Recursive function to navigate sub-directories
run_gradle_tests() {
  for dir in "$1"/*/; do
    if [[ -f "$dir/build.gradle.kts" ]]; then
      echo "Running gradle build test in $dir"
      # add integration test tag
      (cd "$dir" && gradle test)
    fi
    if [[ -d "$dir" ]]; then
      run_gradle_tests "$dir"  # Recursively call function for sub-directories
    fi
  done
}

# Root directory
root_dir="services"

cd kotlin

# Error if the root directory does not exist
if [[ ! -d "$root_dir" ]]; then
  echo "Root directory $root_dir does not exist!"
  exit 1
fi

echo "Starting gradle tests..."
run_gradle_tests "$root_dir"
echo "gradle tests completed."
