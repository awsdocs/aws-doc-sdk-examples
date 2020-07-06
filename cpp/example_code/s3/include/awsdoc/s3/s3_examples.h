// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <awsdoc/s3/S3_EXPORTS.h>

namespace AwsDoc
{
    namespace S3
    {
        AWSDOC_S3_API bool CopyObject(const Aws::String& objectKey, 
            const Aws::String& fromBucket, const Aws::String& toBucket);
        AWSDOC_S3_API bool CreateBucket(const Aws::String& bucketName, 
            const Aws::S3::Model::BucketLocationConstraint& region);
        AWSDOC_S3_API bool DeleteBucket(const Aws::String& bucketName,
            const Aws::String& region);
        AWSDOC_S3_API bool DeleteBucketPolicy(const Aws::String& bucketName);
        AWSDOC_S3_API bool DeleteObject(const Aws::String& objectKey,
            const Aws::String& fromBucket);
        AWSDOC_S3_API bool DeleteBucketWebsite(const Aws::String& bucketName,
            const Aws::String& region);
        AWSDOC_S3_API bool GetBucketAcl(const Aws::String& bucketName,
            const Aws::String& region);
        AWSDOC_S3_API bool GetBucketPolicy(const Aws::String& bucketName,
            const Aws::String& region);
        AWSDOC_S3_API bool GetObjectAcl(const Aws::String& bucketName,
            const Aws::String& objectKey, const Aws::String& region);
        AWSDOC_S3_API bool GetObject(const Aws::String& objectKey,
            const Aws::String& fromBucket); 
        AWSDOC_S3_API bool PutBucketAcl(const Aws::String& bucketName, 
            const Aws::String& region, const Aws::String& ownerID, 
            const Aws::String& granteePermission, 
            const Aws::String& granteeType, 
            Aws::String granteeID, Aws::String granteeDisplayName, 
            Aws::String granteeEmailAddress, Aws::String granteeURI);
        AWSDOC_S3_API bool PutObjectAcl(const Aws::String& bucketName, 
            const Aws::String& objectKey, const Aws::String& region, 
            const Aws::String& ownerID, const Aws::String& granteePermission, 
            const Aws::String& granteeType, Aws::String granteeID, 
            Aws::String granteeDisplayName, 
            Aws::String granteeEmailAddress, Aws::String granteeURI);
    }
}
