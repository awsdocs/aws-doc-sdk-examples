#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes (ex: S3BucketOps) as an argument.'
    exit 1
fi
export CLASSPATH=target/sdk-dynamodb-examples-1.0.jar:$JAVA_SDK_HOME
echo "## Running $1..."
java com.example.dynamodb.$@
