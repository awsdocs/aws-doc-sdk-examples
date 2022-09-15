/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include <aws/core/utils/UUID.h>
#include "awsdoc/s3/s3_demo_for_cloud9.h"
#include "S3_GTests.h"

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_demo_for_cloud9) {
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucketName = "doc-example-bucket-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());

        bool result = FindTheBucket(*s_clientConfig, bucketName);
        ASSERT_TRUE(result);

        result = CreateTheBucket(*s_clientConfig, bucketName);
        ASSERT_TRUE(result);

        result = FindTheBucket(*s_clientConfig, bucketName);
        EXPECT_TRUE(result);

        result = DeleteTheBucket(*s_clientConfig, bucketName);
        if (result) {
            bucketName.clear();
        }
        EXPECT_TRUE(result);

        result = FindTheBucket(*s_clientConfig, bucketName);
        EXPECT_TRUE(result);

        if (!bucketName.empty()) {
            DeleteBucket(bucketName);
        }
    }
} // namespace AwsDocTest
