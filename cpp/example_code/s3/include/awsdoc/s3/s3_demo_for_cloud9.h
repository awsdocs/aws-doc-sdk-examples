// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>

bool FindTheBucket(const Aws::S3::S3Client &s3Client,
                   const Aws::String &bucketName);

bool CreateTheBucket(const Aws::S3::S3Client &s3Client,
                     const Aws::String &bucketName);

bool DeleteTheBucket(const Aws::S3::S3Client &s3Client,
                     const Aws::String &bucketName);