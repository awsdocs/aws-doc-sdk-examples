#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes (ex: ListBuckets) as an argument.'
    exit 1
fi
export CLASSPATH=target/aws-iam-examples-1.0.jar:$JAVA_SDK_HOME/lib/*:$JAVA_SDK_HOME/third-party/lib/*
echo "## Running $1..."
java aws.example.iam.$@

