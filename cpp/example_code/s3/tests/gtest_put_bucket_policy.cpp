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
    // This test requires a user. It fails when running in an EC2 instance that assumes a role.
    // Add the 'U' indicating it only runs in a user environment.
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, put_bucket_policy_2U_) {
        std::vector<Aws::String> bucketNames = GetCachedS3Buckets(BUCKETS_NEEDED);
        ASSERT_GE(bucketNames.size(), BUCKETS_NEEDED)
                                    << "Unable to create bucket as precondition for test" << std::endl;

        Aws::String policyString = GetBucketPolicy(bucketNames[0]);
        ASSERT_TRUE(!policyString.empty()) << "Unable to add policy to bucket as precondition for test" << std::endl;

        bool result = AwsDoc::S3::putBucketPolicy(bucketNames[0], policyString, *s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, put_bucket_policy_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/DeleteBucketPolicy.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;
        Aws::String policyString = R"({
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "1",
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::111111222222:user/UnitTester"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::doc-example-bucket/*"
    }
  ]
})";

        result = AwsDoc::S3::putBucketPolicy("doc-example-bucket", policyString, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
