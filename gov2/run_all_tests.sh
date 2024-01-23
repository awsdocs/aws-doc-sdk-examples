#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

function runCommand() {
  if [ "$1" ] && [ "$1" == 'integration' ]; then
    kind='integration'
  else
    kind='unit'
  fi
  echo "Running $kind tests..."
  for d in /"$2"/*/
    do
      (cd "$d" || exit
      if [ -f go.mod ]; then
        /bin/bash -c "(go test -tags='$1' -timeout=60m ./...)"
      fi)
    done
}

runCommand "$1" "gov2"
runCommand "$1" "gov2/workflows"
