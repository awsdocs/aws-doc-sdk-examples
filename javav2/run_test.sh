#!/bin/bash

# Recursive function to navigate sub-directories
run_mvn_tests() {
  for dir in "$1"/*/; do
    if [[ -f "$dir/pom.xml" ]]; then
      echo "Running mvn test in $dir"
      (cd "$dir" && mvn test)
    fi
    if [[ -d "$dir" ]]; then
      run_mvn_tests "$dir"  # Recursively call function for sub-directories
    fi
  done
}

# Root directory
root_dir="example_code"

# Error if the root directory exists
if [[ ! -d "$root_dir" ]]; then
  echo "Root directory $root_dir does not exist!"
  exit 1
fi

echo "Starting mvn tests..."
run_mvn_tests "$root_dir"
echo "mvn tests completed."
