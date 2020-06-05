// Copyright Amazon.com, Inc. or its affiliates.All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <awsdoc/s3/S3_EXPORTS.h>

namespace AwsDoc
{
    namespace S3
    {
        AWSDOC_S3_API bool CreateBucket(Aws::String bucketName, 
            Aws::S3::Model::BucketLocationConstraint region);
    }
}
