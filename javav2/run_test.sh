#!/bin/bash

# Recursive function to navigate sub-directories
run_mvn_tests() {
  for dir in "$1"/*/; do
    if [[ -d "$dir" ]]; then
      dirname=$(basename "$dir")
      if [[ "$dirname" == "comprehend" && -f "$dir/pom.xml" ]]; then
        echo "Running mvn test in $dir"
        (cd "$dir" && mvn test -Dgroups=weathertop)
      fi
      run_mvn_tests "$dir"  # Recursively call function for sub-directories
    fi
  done
}

cd javav2

# Root directory
root_dir="example_code"

# Error if  root directory does not exist
if [[ ! -d "$root_dir" ]]; then
  echo "Root directory $root_dir does not exist!"
  exit 1
fi

echo "Starting mvn tests..."
run_mvn_tests "$root_dir"
echo "mvn tests completed."
