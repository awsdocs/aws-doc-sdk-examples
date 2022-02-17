// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

// Enables test_list_objects_with_aws_global_region.cpp to test the 
// functionality in list_objects_with_aws_global_region.cpp.

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <awsdoc/s3/S3_EXPORTS.h>

AWSDOC_S3_API bool CreateABucket(const Aws::S3::S3Client& s3Client);
AWSDOC_S3_API bool ListTheObjects(const Aws::S3::S3Client& s3Client);
AWSDOC_S3_API bool DeleteABucket(const Aws::S3::S3Client& s3Client);
