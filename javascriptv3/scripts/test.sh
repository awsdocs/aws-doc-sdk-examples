#!/bin/bash

function run_unit_tests() {
  if ! lerna run test; then
    exit 1
  fi
}

function run_integration_tests() {
  if ! lerna run integration-test; then
    exit 1
  fi
}

if [ "$TEST_SCOPE" == "unit" ]; then
  run_unit_tests
elif [ "$TEST_SCOPE" == "integration" ]; then
  run_integration_tests
else
  run_unit_tests
fi