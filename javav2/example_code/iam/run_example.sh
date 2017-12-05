#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes as an argument.'
    exit 1
fi
export CLASSPATH=target/sdk-iam-examples-1.0.jar:$JAVA_SDK_HOME
echo "## Running $1..."
mvn exec:java -Dexec.mainClass="com.example.iam.$@"

