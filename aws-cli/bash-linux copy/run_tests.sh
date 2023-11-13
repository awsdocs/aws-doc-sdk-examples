#!/bin/bash

base_dir=$(pwd)

echo "Running tests for S3 Bucket Lifecycle Operations"
cd s3/bucket-lifecycle-operations/ || exit
./test_bucket_operations.sh
cd $base_dir || exit

echo "Running IAM tests"
cd iam/tests || exit
./test_iam_examples.sh
cd $base_dir || exit

echo "Running medical-imaging tests"
cd medical-imaging/tests || exit
./test_medical_imaging_examples.sh
cd $base_dir || exit

echo "Done"