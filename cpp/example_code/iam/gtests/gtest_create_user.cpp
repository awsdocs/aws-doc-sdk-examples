/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_user) {
        auto userName = uuidName("user");
        auto result = AwsDoc::IAM::createUser(userName, *s_clientConfig);
        ASSERT_TRUE(result);

        deleteUser(userName);
    }
} // namespace AwsDocTest
