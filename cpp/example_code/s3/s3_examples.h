// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0 

#pragma once

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <cstdint>

namespace AwsDoc {
    namespace S3 {
        bool
        copyObject(const Aws::String &objectKey, const Aws::String &fromBucket,
                   const Aws::String &toBucket,
                   const Aws::S3::S3ClientConfiguration &clientConfig);

        bool createBucket(const Aws::String &bucketName,
                          const Aws::S3::S3ClientConfiguration &clientConfig);

        bool deleteObjects(const std::vector<Aws::String> &objectKeys,
                           const Aws::String &fromBucket,
                           const Aws::S3::S3ClientConfiguration &clientConfig);

        bool deleteBucket(const Aws::String &bucketName,
                          const Aws::S3::S3ClientConfiguration &clientConfig);

        bool deleteBucketPolicy(const Aws::String &bucketName,
                                const Aws::S3::S3ClientConfiguration &clientConfig);

        bool deleteObject(const Aws::String &objectKey,
                          const Aws::String &fromBucket,
                          const Aws::S3::S3ClientConfiguration &clientConfig);

        bool deleteBucketWebsite(const Aws::String &bucketName,
                                 const Aws::S3::S3ClientConfiguration &clientConfig);

        bool getBucketAcl(const Aws::String &bucketName,
                          const Aws::S3::S3ClientConfiguration &clientConfig);

        bool getBucketPolicy(const Aws::String &bucketName,
                             const Aws::S3::S3ClientConfiguration &clientConfig);

        bool GetObjectAcl(const Aws::String &bucketName,
                          const Aws::String &objectKey,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool getObject(const Aws::String &objectKey,
                       const Aws::String &fromBucket,
                       const Aws::S3::S3ClientConfiguration &clientConfig);

        bool GetWebsiteConfig(const Aws::String &bucketName,
                              const Aws::Client::ClientConfiguration &clientConfig);

        bool ListBuckets(const Aws::Client::ClientConfiguration &clientConfig);

        bool ListBucketDisablingDnsCache(
                const Aws::Client::ClientConfiguration &clientConfig);

        bool ListObjects(const Aws::String &bucketName,
                         const Aws::Client::ClientConfiguration &clientConfig);

        bool ListObjectsWithAWSGlobalRegion(
                const Aws::Client::ClientConfiguration &clientConfig);

        Aws::String GeneratePreSignedPutObjectURL(const Aws::String &bucketName,
                                                  const Aws::String &key,
                                                  uint64_t expirationSeconds,
                                                  const Aws::Client::ClientConfiguration &clientConfig);

        Aws::String GeneratePreSignedGetObjectURL(const Aws::String &bucketName,
                                                  const Aws::String &key,
                                                  uint64_t expirationSeconds,
                                                  const Aws::Client::ClientConfiguration &clientConfig);

        bool
        PutBucketAcl(const Aws::String &bucketName, const Aws::String &ownerID, const Aws::String &granteePermission,
                     const Aws::String &granteeType, const Aws::String &granteeID,
                     const Aws::String &granteeEmailAddress,
                     const Aws::String &granteeURI, const Aws::Client::ClientConfiguration &clientConfig);

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
                              const Aws::String &indexPage,
                              const Aws::String &errorPage,
                              const Aws::Client::ClientConfiguration &clientConfig);

        bool S3_GettingStartedScenario(const Aws::String &uploadFilePath,
                                       const Aws::String &saveFilePath,
                                       const Aws::Client::ClientConfiguration &clientConfig);

        bool getPutBucketAcl(const Aws::String &bucketName,
                             const Aws::String &ownerID,
                             const Aws::String &granteePermission,
                             const Aws::String &granteeType,
                             const Aws::String &granteeID,
                             const Aws::String &granteeEmailAddress,
                             const Aws::String &granteeURI,
                             const Aws::S3::S3ClientConfiguration &clientConfig);

        extern std::mutex upload_mutex;

        extern std::condition_variable upload_variable;

        bool PutStringWithPresignedObjectURL(const Aws::String &presignedURL,
                                             const Aws::String &data);

        bool GetObjectWithPresignedObjectURL(const Aws::String &presignedURL,
                                             Aws::String &resultString);
    } // namespace S3
} // namespace AwsDoc
