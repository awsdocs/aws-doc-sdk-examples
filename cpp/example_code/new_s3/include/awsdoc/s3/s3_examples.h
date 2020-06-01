/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once
#include <awsdoc/s3/S3_EXPORTS.h>

namespace AwsDoc
{
    namespace S3
    {
        AWSDOC_S3_API bool ListBuckets();
        AWSDOC_S3_API bool ListObjects(const char* bucketName);
    }
}
