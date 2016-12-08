#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes (ex: CreateTable) as an argument.'
    exit 1
fi
export CLASSPATH=target/aws-dynamodb-examples-1.0.jar:$JAVA_SDK_HOME/lib/*:$JAVA_SDK_HOME/third-party/lib/*
echo "## Running $1..."
java aws.example.dynamodb.$@

