// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

// Enables test_s3-demo.pp to test the functionality in s3-demo.cpp.

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <awsdoc/s3/S3_EXPORTS.h>

AWSDOC_S3_API bool FindTheBucket(const Aws::S3::S3Client& s3Client,
    const Aws::String& bucketName);
AWSDOC_S3_API bool CreateTheBucket(const Aws::S3::S3Client& s3Client,
    const Aws::String& bucketName);
AWSDOC_S3_API bool DeleteTheBucket(const Aws::S3::S3Client& s3Client,
    const Aws::String& bucketName);