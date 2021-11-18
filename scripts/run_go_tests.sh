#!/usr/bin/env bash
# Run tests

hasgotest=0

for f in $@ ; do
#    echo Looking at $f
    # Do any end with "_test.go"?
    path="$(dirname $f)"
    file="$(basename $f)"
    echo $pa
    if [[ $f =~ /gov2/.+ ]]; then
        hasgotest=1
    fi
done

echo $hasgotest

if [[ $hasgotest -eq 1 ]]; then
    echo "Go files have been modified. Running tests..."
    for mod in $( find . -name go.mod ); do
        moddir=$(dirname $mod)
        echo "Testing ${moddir}"
        pushd $moddir
        go test ./...
        if [[ $? -ne 0 ]]; then
            exit 0
        fi
        popd
    done
fi

exit 0
