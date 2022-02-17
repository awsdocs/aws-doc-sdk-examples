#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes as an argument.'
    echo 'If there are arguments to the class, put them in quotes after the class name.'
    exit 1
fi
export CLASSPATH=target/sdk-mediaconvert-examples-1.0.jar:target/lib/*:
export className=$1
echo "## Running $className..."
shift
echo "## arguments $@..."
echo "CLASSPATH:" $CLASSPATH
mvn exec:java -Dexec.mainClass="com.example.mediaconvert.$className" -Dexec.args="$@"