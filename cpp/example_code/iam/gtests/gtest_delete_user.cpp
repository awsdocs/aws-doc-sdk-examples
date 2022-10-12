/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, delete_user) {
        auto userName = createUser();
        ASSERT_FALSE(userName.empty()) << preconditionError() << std::endl;

        auto result = AwsDoc::IAM::deleteUser(userName, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
