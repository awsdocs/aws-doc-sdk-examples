#!/bin/bash

run_unit_tests() {
  npm run test --workspaces --if-present -- --silent
}

run_integration_tests() {
  npm run integration-test --workspaces --if-present -- --silent
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
  run_integration_tests
else
  echo "Usage: $0 [unit|integration]"
  exit 1
fi
