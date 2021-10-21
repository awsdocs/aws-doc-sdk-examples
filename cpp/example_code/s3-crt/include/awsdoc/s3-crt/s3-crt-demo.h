// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// Enables test_s3-crt-demo.cpp to test the functionality in s3-crt-demo.cpp.

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3-crt/S3CrtClient.h>
#include <awsdoc/s3-crt/S3Crt_EXPORTS.h>

AWSDOC_S3CRT_API bool ListBuckets(const Aws::S3Crt::S3CrtClient& s3CrtClient,
    const Aws::String& bucketName);
AWSDOC_S3CRT_API bool CreateBucket(const Aws::S3Crt::S3CrtClient& s3CrtClient,
    const Aws::String& bucketName,
    const Aws::S3Crt::Model::BucketLocationConstraint& locConstraint);
AWSDOC_S3CRT_API bool DeleteBucket(const Aws::S3Crt::S3CrtClient& s3CrtClient,
    const Aws::String& bucketName);
AWSDOC_S3CRT_API bool PutObject(const Aws::S3Crt::S3CrtClient& s3CrtClient,
    const Aws::String& bucketName, const Aws::String& objectKey, const Aws::String& fileName);
AWSDOC_S3CRT_API bool GetObject(const Aws::S3Crt::S3CrtClient& s3CrtClient,
    const Aws::String& bucketName, const Aws::String& objectKey);
AWSDOC_S3CRT_API bool DeleteObject(const Aws::S3Crt::S3CrtClient& s3CrtClient,
    const Aws::String& bucketName, const Aws::String& objectKey);
