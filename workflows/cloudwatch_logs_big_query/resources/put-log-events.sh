#!/bin/bash

FILES=$(ls file*.json)

for f in $FILES; do
    echo "$f"
    aws logs put-log-events --log-group-name "/workflows/cloudwatch-log/big-query" --log-stream-name stream1 --log-events file://"$f" --no-cli-pager
done