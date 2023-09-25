#!/bin/bash

function runCommand() {
  dt=$(date +%Y-%m-%d)
  if [ $@ ] && [ $@ == 'integration' ]
  then
    kind='integration'
  else
    kind='unit'
  fi
  echo Running $kind tests...
  for d in ./*/ ; do /bin/bash -c "(cd '$d' && go test -tags=$@  -timeout=60m ./...)"; done
}

runCommand $1
