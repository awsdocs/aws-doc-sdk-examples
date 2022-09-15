/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef S3_EXAMPLES_S3_GTESTS_H
#define S3_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <memory>
#include <gtest/gtest.h>

namespace AwsDocTest {

    class S3_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        // returns a vector of buckets that are maintained by this class.
        static std::vector<Aws::String> GetCachedS3Buckets(size_t count);

        static Aws::String GetTestFilePath();

        static bool DeleteObjectInBucket(const Aws::String &bucketName, const Aws::String &objectName);

        static bool DeleteAllObjectsInBucket(const Aws::String &bucketName);

        static bool DeleteBucket(const Aws::String &bucketName);

        static bool CreateBucket(const Aws::String &bucketName);

        static Aws::String GetArnForUser();

        static Aws::String PutTestFileInBucket(const Aws::String &bucketName);

        static Aws::String GetBucketPolicy(const Aws::String &bucketName);

        static bool AddPolicyToBucket(const Aws::String &bucketName);

        static bool PutWebsiteConfig(const Aws::String &bucketName);

        static Aws::String GetCanonicalUserID();

        // s_clientConfig must be a pointer because the client config must be initialized after InitAPI
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

    private:
        static void DeleteBuckets();

        static Aws::SDKOptions s_options;
        static std::vector<Aws::String> s_cachedS3Buckets;
        static Aws::String s_testFilePath;
        static Aws::String s_canonicalUserID;
        static Aws::String s_userArn;

        std::stringbuf m_coutBuffer;  // used just to silence cout
        std::streambuf *m_savedBuffer = nullptr;
    };
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
