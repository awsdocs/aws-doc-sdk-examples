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
#include <aws/core/utils/UUID.h>
#include "S3_GTests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, delete_bucket_2_) {
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String bucketName = "doc-example-bucket-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());


        bool result = CreateBucket(bucketName);
        ASSERT_TRUE(result) << "Unable to create bucket as precondition for test" << std::endl;

        result = AwsDoc::S3::DeleteBucket(bucketName, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
