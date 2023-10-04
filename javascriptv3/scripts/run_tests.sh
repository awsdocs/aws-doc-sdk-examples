#!/bin/bash

run_unit_tests() {
  # Write stdout to a file and stderr to stdout
  npm run test --workspaces --if-present -- --silent 2>&1 > unit_test.log 
}

run_integration_tests() {
  # Write stdout to a file and stderr to stdout
  npm run integration-test --workspaces --if-present --silent 2>&1 > integration_test.log
}

run_all() {
  if ! run_unit_tests || ! run_integration_tests; then
  exit 1
  fi
}

if [[ $# -eq 0 ]]; then
  run_unit_tests
elif [[ "$1" == "unit" && "$2" == "integration" ]] || [[ "$1" == "integration" && "$2" == "unit" ]]; then
  run_all
elif [[ "$1" == "unit" || "$2" == "unit" ]]; then
  run_unit_tests
elif [[ "$1" == "integration" || "$2" == "integration" ]]; then
  run_unit_tests
else
  echo "Usage: $0 [unit|integration]"
  exit 1
fi
