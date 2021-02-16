#!/bin/bash

if [ "$1" == "" ]
then
    echo You must supply the name of a Lambda function
else
    aws lambda invoke  --function-name $1 --payload file://dynamodb-payload.json output
fi
