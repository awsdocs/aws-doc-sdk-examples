#!/bin/bash

if [ "$TEST_SCOPE" == "unit" ]; then
  lerna run test
elif [ "$TEST_SCOPE" == "integration" ]; then
  lerna run integration-test
fi

if [ -z $TEST_SCOPE ]; then
   if ! lerna run test ; then
     exit 1
   fi
   if ! lerna run integration-test ; then
     exit 1
   fi
fi