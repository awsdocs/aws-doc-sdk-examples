#!/bin/bash

function runCommand() {
  if [ "$1" ] && [ "$1" == 'integration' ]; then
    kind='integration'
  else
    kind='unit'
  fi
  echo "Running $kind tests..."
  for d in /gov2/*/ ; do
    /bin/bash -c "(cd '$d' && go test -tags='$1' -timeout=60m ./...)"
  done
}

runCommand "$1"
