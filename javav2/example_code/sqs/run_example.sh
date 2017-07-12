#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes (ex: SQSExample) as an argument.'
    exit 1
fi
export CLASSPATH=target/sdk-sqs-examples-1.0.jar:$JAVA_SDK_HOME/
echo "## Running $1..."
java com.example.sqs.$@
