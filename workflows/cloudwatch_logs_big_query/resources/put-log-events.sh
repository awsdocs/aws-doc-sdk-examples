#!/bin/bash

FILES=$(ls file*.json)

for f in $FILES; do
    echo "$f"
    
    LOG_GROUP_NAME=""
    
    if [[ "$OSTYPE" == "msys" ]]; then
        LOG_GROUP_NAME="//workflows\\cloudwatch-log\\big-query"
    else
        LOG_GROUP_NAME="/workflows/cloudwatch-log/big-query"
    fi

    aws logs put-log-events --log-group-name "$LOG_GROUP_NAME" --log-stream-name stream1 --log-events file://"$f" --no-cli-pager
done