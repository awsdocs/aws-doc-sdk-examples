#!/bin/bash

function run_unit_tests() {
  if ! npm run test --workspaces --if-present; then
    exit 1
  fi
}

function run_integration_tests() {
  if ! npm run integration-test --workspaces --if-present; then
    exit 1
  fi
}

if [ -z "$1" ]; then
  echo "Usage: $0 [unit|integration]"
  exit 1
elif [ "$1" == "unit" ]; then
  run_unit_tests
elif [ "$1" == "integration" ]; then
  run_integration_tests
else
  echo "Usage: $0 [unit|integration]"
  exit 1
fi
