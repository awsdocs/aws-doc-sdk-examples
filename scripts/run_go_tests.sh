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
    hasfailedtest=0
    for mod in $( find . -name go.mod ); do
        moddir=$(dirname $mod)
        pushd $moddir
        if [[ -f .nocitest ]]; then
            echo "Skipping ${moddir} - found .nocitest"
        else
            echo "Testing ${moddir} via go test"
            go test ./...
            if [[ $? -ne 0 ]]; then
                hasfailedtest=1
            fi
        fi
        popd
    done
    exit $hasfailedtest
fi

exit 0
