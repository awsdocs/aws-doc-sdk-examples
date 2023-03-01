/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
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
#include "awsdoc/s3/s3_examples.h"
#include "S3_GTests.h"

static const int BUCKETS_NEEDED = 1;

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, get_object_acl_2_) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED)
                                    << "Failed to meet precondition" << std::endl;

        Aws::String testFile = PutTestFileInBucket(bucketNames[0]);
        ASSERT_TRUE(!testFile.empty()) << "Failed to meet precondition" << std::endl;

        bool result = AwsDoc::S3::GetObjectAcl(bucketNames[0], testFile,
                                               *s_clientConfig);

        EXPECT_TRUE(result);

        DeleteObjectInBucket(bucketNames[0], testFile);
    }


    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, put_object_acl_2_) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED)
                                    << "Failed to meet precondition" << std::endl;

        Aws::String testFile = PutTestFileInBucket(bucketNames[0]);
        ASSERT_TRUE(!testFile.empty()) << "Failed to meet precondition" << std::endl;

        Aws::String canonicalUserID = GetCanonicalUserID();
        ASSERT_TRUE(!canonicalUserID.empty())
                                    << "Failed to meet precondition" << std::endl;

        bool result = AwsDoc::S3::PutObjectAcl(bucketNames[0], testFile,
                                               canonicalUserID, "READ",
                                               "Canonical user",
                                               canonicalUserID,
                                               *s_clientConfig);

        EXPECT_TRUE(result);

        DeleteObjectInBucket(bucketNames[0], testFile);
    } // namespace AwsDocTest

} // namespace AwsDocTest
