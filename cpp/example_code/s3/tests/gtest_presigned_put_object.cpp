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
    TEST_F(S3_GTests, presigned_put_object_2_) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED) << "Failed to meet precondition" << std::endl;

        Aws::String key = "presigned_put.txt";
        Aws::String presignedURL = AwsDoc::S3::generatePreSignedPutObjectUrl(bucketNames[0], key, 10 * 60,
                                                                             *s_clientConfig);

        EXPECT_TRUE(!presignedURL.empty());

#if HAS_CURL
        bool result = AwsDoc::S3::PutStringWithPresignedObjectURL(presignedURL, "Some text");
        EXPECT_TRUE(result);

        DeleteObjectInBucket(bucketNames[0], key);
#endif
    }

} // namespace AwsDocTest
