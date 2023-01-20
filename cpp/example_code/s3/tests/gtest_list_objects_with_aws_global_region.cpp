/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "awsdoc/s3/s3_examples.h"
#include <aws/core/utils/UUID.h>
#include "S3_GTests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, list_objects_with_aws_global_region) {

        bool result = AwsDoc::S3::ListObjectsWithAWSGlobalRegion(*s_clientConfig);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest
