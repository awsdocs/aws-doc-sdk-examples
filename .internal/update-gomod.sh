#!/usr/bin/env bash

for gomod in $(find . -name go.mod); do
    MODROOT=$(dirname $gomod)
    echo "Updating dependencies in $MODROOT"
    pushd $MODROOT
    go get -u
    go mod tidy
    popd
done