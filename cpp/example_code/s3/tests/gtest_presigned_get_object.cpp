// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include <fstream>
#include "../s3_examples.h"
#include "S3_GTests.h"

static const int BUCKETS_NEEDED = 1;

namespace AwsDocTest {

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, presigned_get_object_2_) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED) << "Failed to meet precondition" << std::endl;

        Aws::String fileName = PutTestFileInBucket(bucketNames[0]);
        ASSERT_TRUE(!fileName.empty()) << "Failed to meet precondition" << std::endl;

        Aws::String presignedURL = AwsDoc::S3::generatePreSignedGetObjectUrl(bucketNames[0], fileName, 10 * 60,
                                                                             *s_clientConfig);

        EXPECT_TRUE(!presignedURL.empty());

#if HAS_CURL
        Aws::String stringResult;
        bool result = AwsDoc::S3::getObjectWithPresignedObjectUrl(presignedURL, stringResult);
        EXPECT_TRUE(result);

        DeleteObjectInBucket(bucketNames[0], stringResult);
#endif
    }

} // namespace AwsDocTest
