// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <awsdoc/s3/S3_EXPORTS.h>

namespace AwsDoc
{
    namespace S3
    {
        AWSDOC_S3_API bool CopyObject(const Aws::String& objectKey, 
            const Aws::String& fromBucket, const Aws::String& toBucket, 
            const Aws::String& region = "");
        AWSDOC_S3_API bool CreateBucket(const Aws::String& bucketName, 
            const Aws::S3::Model::BucketLocationConstraint& region);
        AWSDOC_S3_API bool DeleteBucket(const Aws::String& bucketName,
            const Aws::String& region = "");
        AWSDOC_S3_API bool DeleteBucketPolicy(const Aws::String& bucketName, 
            const Aws::String& region = "");
        AWSDOC_S3_API bool DeleteObject(const Aws::String& objectKey,
            const Aws::String& fromBucket, const Aws::String& region = "");
        AWSDOC_S3_API bool DeleteBucketWebsite(const Aws::String& bucketName,
            const Aws::String& region = "");
        AWSDOC_S3_API bool GetBucketAcl(const Aws::String& bucketName,
            const Aws::String& region = "");
        AWSDOC_S3_API bool GetBucketPolicy(const Aws::String& bucketName,
            const Aws::String& region = "");
        AWSDOC_S3_API bool GetObjectAcl(const Aws::String& bucketName,
            const Aws::String& objectKey, const Aws::String& region = "");
        AWSDOC_S3_API bool GetObject(const Aws::String& objectKey,
            const Aws::String& fromBucket, const Aws::String& region = "");
        AWSDOC_S3_API bool GetWebsiteConfig(const Aws::String& bucketName,
            const Aws::String& region = "");
        AWSDOC_S3_API bool ListBuckets();
        AWSDOC_S3_API bool ListObjects(const Aws::String& bucketName, 
            const Aws::String& region = "");
        AWSDOC_S3_API bool PutBucketAcl(const Aws::String& bucketName, 
            const Aws::String& ownerID, 
            const Aws::String& granteePermission, 
            const Aws::String& granteeType,
            const Aws::String& region, 
            const Aws::String& granteeID = "", 
            const Aws::String& granteeDisplayName = "",
            const Aws::String& granteeEmailAddress = "",
            const Aws::String& granteeURI = "");
        AWSDOC_S3_API bool PutBucketPolicy(const Aws::String& bucketName,
            const Aws::String& policyBody, const Aws::String& region = "");
        AWSDOC_S3_API bool PutObject(const Aws::String& bucketName,
            const Aws::String& objectName,
            const Aws::String& region = "");
        AWSDOC_S3_API bool PutObjectAcl(const Aws::String& bucketName, 
            const Aws::String& objectKey,
            const Aws::String& region, 
            const Aws::String& ownerID,
            const Aws::String& granteePermission, 
            const Aws::String& granteeType, 
            const Aws::String& granteeID = "",
            const Aws::String& granteeDisplayName = "",
            const Aws::String& granteeEmailAddress = "",
            const Aws::String& granteeURI = "");
        AWSDOC_S3_API bool PutObjectAsync(const Aws::S3::S3Client& s3Client,
            const Aws::String& bucketName,
            const Aws::String& objectName,
            const Aws::String& region);
        AWSDOC_S3_API bool PutObjectBuffer(const Aws::String& bucketName,
            const Aws::String& objectName,
            const std::string& objectContent,
            const Aws::String& region);
        AWSDOC_S3_API bool PutWebsiteConfig(const Aws::String& bucketName,
            const Aws::String& indexPage, const Aws::String& errorPage,
            const Aws::String& region = "");
        AWSDOC_S3_API bool S3Scenario(const Aws::String& bucketName,
             const Aws::String& key, const Aws::String& objectPath,
             const Aws::String& savePath, const Aws::String& toBucket,
             const Aws::Client::ClientConfiguration &clientConfig);
    }
}
