#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

function runCommand() {
  echo Linting Go files...
  for d in ./*/
  do
    echo Linting "$d"...
    (cd "$d" || exit
    golangci-lint run
    ret_code=$?
    if [ $ret_code != 0 ]; then exit $ret_code; else echo success; fi)
  done
}

runCommand
