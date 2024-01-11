#!/bin/bash

function runCommand() {
  if [ "$@" ] && [ "$@" == 'integration' ]
  then
    kind='integration'
  else
    kind='unit'
  fi
  echo Running $kind tests...
  for d in /gov2/*/ ; do /bin/bash -c "(cd '$d' && go test -tags=$@  -timeout=60m ./...)"; done
}

runCommand "$1"
