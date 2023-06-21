#!/bin/bash

function runCommand() {
  echo Linting Go files...
  # When all Go examples have been updated, change this allow list to ./*/.
  for d in aurora demotools dynamodb iam lambda s3 testtools
  do
    cd $d
    golangci-lint run
    ret_code=$?
    if [ $ret_code != 0 ]; then exit $ret_code; fi
    cd ..
  done
}

runCommand
