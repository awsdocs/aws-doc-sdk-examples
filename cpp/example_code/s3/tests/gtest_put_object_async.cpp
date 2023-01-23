/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include <aws/s3/S3Client.h>
#include "awsdoc/s3/s3_examples.h"
#include "S3_GTests.h"

static const int BUCKETS_NEEDED = 1;

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, put_object_async) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED) << "Failed to meet precondition" << std::endl;

        Aws::String testFile = GetTestFilePath();
        ASSERT_TRUE(!testFile.empty()) << "Failed to meet precondition" << std::endl;

        {
            Aws::S3::S3Client client(*s_clientConfig);
            std::unique_lock<std::mutex> lock(AwsDoc::S3::upload_mutex);
            bool result = AwsDoc::S3::PutObjectAsync(client, bucketNames[0], testFile);

            AwsDoc::S3::upload_variable.wait(lock);

            EXPECT_TRUE(result);
        }

        DeleteObjectInBucket(bucketNames[0], testFile);
    }
} // namespace AwsDocTest
