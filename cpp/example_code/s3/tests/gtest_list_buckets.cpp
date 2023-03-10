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

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, list_buckets_2_) {

        bool result = AwsDoc::S3::ListBuckets(*s_clientConfig);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest
