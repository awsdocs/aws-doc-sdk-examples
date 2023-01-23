#!/bin/bash

if [ "$TEST_SCOPE" == "unit" ]; then
  lerna run test
elif [ "$TEST_SCOPE" == "integration" ]; then
  lerna run integration-test
fi

if [ -z $TEST_SCOPE ]; then
  lerna run test
  lerna run integration-test
fi