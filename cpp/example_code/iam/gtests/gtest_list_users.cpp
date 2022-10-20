/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, list_users) {
        auto result = AwsDoc::IAM::listUsers(*s_clientConfig);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest
