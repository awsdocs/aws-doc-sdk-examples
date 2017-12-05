#!/bin/bash
if [[ -z $* ]] ; then
    echo 'Supply the name of one of the example classes (ex: S3BucketOps) as an argument.'
    exit 1
fi
export CLASSPATH=target/sdk-s3-examples-1.0.jar:$JAVA_SDK_HOME
echo "## Running $1..."
mvn exec:java -Dexec.mainClass="com.example.s3.$@"
