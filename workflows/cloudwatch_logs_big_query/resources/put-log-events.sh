#!/bin/bash

FILES=$(ls file*.json)

for f in $FILES; do
    fnum=$(echo "$f" | sed -e 's/^file//' -e 's/\.json$//')
    echo $f
    aws logs put-log-events --log-group-name "/workflows/cloudwatch-log/big-query" --log-stream-name stream1 --log-events file://$f --no-cli-pager
done