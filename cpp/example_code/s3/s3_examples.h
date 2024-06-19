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

        bool getObjectAcl(const Aws::String &bucketName,
                          const Aws::String &objectKey,
                          const Aws::S3::S3ClientConfiguration &clientConfig);

        bool getObject(const Aws::String &objectKey,
                       const Aws::String &fromBucket,
                       const Aws::S3::S3ClientConfiguration &clientConfig);

        bool getWebsiteConfig(const Aws::String &bucketName,
                              const Aws::S3::S3ClientConfiguration &clientConfig);

        bool listBuckets(const Aws::S3::S3ClientConfiguration &clientConfig);

        bool listBucketDisablingDnsCache(
                const Aws::S3::S3ClientConfiguration &clientConfig);

        bool ListObjects(const Aws::String &bucketName,
                         const Aws::S3::S3ClientConfiguration &clientConfig);

        bool listObjectsWithAwsGlobalRegion(
                const Aws::S3::S3ClientConfiguration &clientConfig);

        Aws::String generatePreSignedPutObjectUrl(const Aws::String &bucketName,
                                                  const Aws::String &key,
                                                  uint64_t expirationSeconds,
                                                  const Aws::S3::S3ClientConfiguration &clientConfig);

        Aws::String generatePreSignedGetObjectUrl(const Aws::String &bucketName,
                                                  const Aws::String &key,
                                                  uint64_t expirationSeconds,
                                                  const Aws::S3::S3ClientConfiguration &clientConfig);

        bool
        putBucketAcl(const Aws::String &bucketName, const Aws::String &ownerID, const Aws::String &granteePermission,
                     const Aws::String &granteeType, const Aws::String &granteeID,
                     const Aws::String &granteeEmailAddress,
                     const Aws::String &granteeURI, const Aws::S3::S3ClientConfiguration &clientConfig);

        bool putBucketPolicy(const Aws::String &bucketName,
                             const Aws::String &policyBody,
                             const Aws::S3::S3ClientConfiguration &clientConfig);

        bool putObject(const Aws::String &bucketName,
                       const Aws::String &fileName,
                       const Aws::S3::S3ClientConfiguration &clientConfig);

        bool putObjectAcl(const Aws::String &bucketName, const Aws::String &objectKey, const Aws::String &ownerID,
                          const Aws::String &granteePermission, const Aws::String &granteeType,
                          const Aws::String &granteeID, const Aws::String &granteeEmailAddress,
                          const Aws::String &granteeURI, const Aws::S3::S3ClientConfiguration &clientConfig);

        bool putObjectAsync(const Aws::S3::S3Client &s3Client,
                            const Aws::String &bucketName,
                            const Aws::String &fileName);

        bool putObjectBuffer(const Aws::String &bucketName,
                             const Aws::String &objectName,
                             const std::string &objectContent,
                             const Aws::S3::S3ClientConfiguration &clientConfig);

        bool putWebsiteConfig(const Aws::String &bucketName,
                              const Aws::String &indexPage,
                              const Aws::String &errorPage,
                              const Aws::S3::S3ClientConfiguration &clientConfig);

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

        bool getObjectWithPresignedObjectUrl(const Aws::String &presignedURL,
                                             Aws::String &resultString);
    } // namespace S3
} // namespace AwsDoc
