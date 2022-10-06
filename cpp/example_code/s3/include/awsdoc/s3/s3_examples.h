// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/BucketLocationConstraint.h>

namespace AwsDoc {
    namespace S3 {
        bool
        CopyObject(const Aws::String &objectKey, const Aws::String &fromBucket, const Aws::String &toBucket,
                   const Aws::Client::ClientConfiguration &clientConfig);

        bool CreateBucket(const Aws::String &bucketName,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool DeleteBucket(const Aws::String &bucketName,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool DeleteBucketPolicy(const Aws::String &bucketName,
                                const Aws::Client::ClientConfiguration &clientConfig);

        bool DeleteObject(const Aws::String &objectKey,
                          const Aws::String &fromBucket,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool DeleteBucketWebsite(const Aws::String &bucketName,
                                 const Aws::Client::ClientConfiguration &clientConfig);

        bool GetBucketAcl(const Aws::String &bucketName,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool GetBucketPolicy(const Aws::String &bucketName,
                             const Aws::Client::ClientConfiguration &clientConfig);

        bool GetObjectAcl(const Aws::String &bucketName,
                          const Aws::String &objectKey,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool GetObject(const Aws::String &objectKey,
                       const Aws::String &fromBucket,
                       const Aws::Client::ClientConfiguration &clientConfig);

        bool GetWebsiteConfig(const Aws::String &bucketName,
                              const Aws::Client::ClientConfiguration &clientConfig);

        bool ListBuckets(const Aws::Client::ClientConfiguration &clientConfig);

        bool ListBucketDisablingDnsCache(const Aws::Client::ClientConfiguration &clientConfig);

        bool ListObjects(const Aws::String &bucketName,
                         const Aws::Client::ClientConfiguration &clientConfig);

        bool ListObjectsWithAWSGlobalRegion(const Aws::Client::ClientConfiguration &clientConfig);

        bool PutBucketAcl(const Aws::String &bucketName,
                          const Aws::String &ownerID,
                          const Aws::String &granteePermission,
                          const Aws::String &granteeType,
                          const Aws::String &granteeID,
                          const Aws::Client::ClientConfiguration &clientConfig,
                          const Aws::String &granteeDisplayName = "",
                          const Aws::String &granteeEmailAddress = "",
                          const Aws::String &granteeURI = "");

        bool PutBucketPolicy(const Aws::String &bucketName,
                             const Aws::String &policyBody,
                             const Aws::Client::ClientConfiguration &clientConfig);

        bool PutObject(const Aws::String &bucketName,
                       const Aws::String &fileName,
                       const Aws::Client::ClientConfiguration &clientConfig);

        bool PutObjectAcl(const Aws::String &bucketName,
                          const Aws::String &objectKey,
                          const Aws::String &ownerID,
                          const Aws::String &granteePermission,
                          const Aws::String &granteeType,
                          const Aws::String &granteeID,
                          const Aws::Client::ClientConfiguration &clientConfig,
                          const Aws::String &granteeDisplayName = "",
                          const Aws::String &granteeEmailAddress = "",
                          const Aws::String &granteeURI = "");

        bool PutObjectAsync(const Aws::S3::S3Client &s3Client,
                            const Aws::String &bucketName,
                            const Aws::String &fileName);

        bool PutObjectBuffer(const Aws::String &bucketName,
                             const Aws::String &objectName,
                             const std::string &objectContent,
                             const Aws::Client::ClientConfiguration &clientConfig);

        bool PutWebsiteConfig(const Aws::String &bucketName,
                              const Aws::String &indexPage, const Aws::String &errorPage,
                              const Aws::Client::ClientConfiguration &clientConfig);

        bool S3_GettingStartedScenario(const Aws::String &uploadFilePath, const Aws::String &saveFilePath,
                                       const Aws::Client::ClientConfiguration &clientConfig);

        bool GetPutBucketAcl(const Aws::String &bucketName,
                             const Aws::String &ownerID,
                             const Aws::String &granteePermission,
                             const Aws::String &granteeType,
                             const Aws::String &granteeID,
                             const Aws::Client::ClientConfiguration &clientConfig,
                             const Aws::String &granteeDisplayName = "",
                             const Aws::String &granteeEmailAddress = "",
                             const Aws::String &granteeURI = "");

        extern std::mutex upload_mutex;

        extern std::condition_variable upload_variable;
    } // namespace S3
} // namespace AwsDoc
