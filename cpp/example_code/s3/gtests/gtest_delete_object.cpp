/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "awsdoc/s3/s3_examples.h"
#include <aws/core/utils/UUID.h>
#include "S3_GTests.h"

static const int BUCKETS_NEEDED = 1;

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, delete_object) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED) << "Failed to meet precondition" << std::endl;

        Aws::String fileName = PutTestFileInBucket(bucketNames[0]);
        ASSERT_TRUE(!fileName.empty()) << "Failed to meet precondition" << std::endl;

        bool result = AwsDoc::S3::DeleteObject(fileName, bucketNames[0], *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
