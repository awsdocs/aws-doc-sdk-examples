/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "awsdoc/s3/s3_examples.h"
#include "S3_GTests.h"

static const int BUCKETS_NEEDED = 2;

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, copy_object) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED) << "Failed to meet precondition" << std::endl;

        Aws::String fileName = PutTestFileInBucket(bucketNames[0]);
        ASSERT_TRUE(!fileName.empty()) << "Failed to meet precondition" << std::endl;

        bool result = AwsDoc::S3::CopyObject(Aws::String(fileName), bucketNames[0],
                                             bucketNames[1], *s_clientConfig);

        EXPECT_TRUE(result);

        DeleteObjectInBucket(bucketNames[0], fileName);
        DeleteObjectInBucket(bucketNames[1], fileName);
    }
} // namespace AwsDocTest
