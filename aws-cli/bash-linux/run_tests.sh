#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

base_dir=$(pwd)

echo "Running tests for S3 Bucket Lifecycle Operations"
cd "$base_dir/s3/bucket-lifecycle-operations/" || exit
./test_bucket_operations.sh

echo "Running IAM tests"
cd "$base_dir/iam/tests" || exit
./test_iam_examples.sh

echo "Running medical-imaging tests"
cd "$base_dir/medical-imaging/tests" || exit
./test_medical_imaging_examples.sh

echo "Done"