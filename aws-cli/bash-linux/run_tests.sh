#!/bin/bash

base_dir=$(pwd)

echo "Running tests for S3 Bucket Lifecycle Operations"
cd s3/bucket-lifecycle-operations/
./test_bucket_operations.sh
cd $base_dir

echo "Running IAM tests"
cd iam/tests
./test_iam_examples.sh
cd $base_dir

echo "Running medical-imaging tests"
cd medical-imaging/tests
./test_medical_imaging_examples.sh
cd $base_dir

echo "Done"