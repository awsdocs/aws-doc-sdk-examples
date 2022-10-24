/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, update_user) {
        auto user = getUser();
        ASSERT_FALSE(user.empty());
        auto newUserName = uuidName("user");

        auto result = AwsDoc::IAM::updateUser(user, newUserName, *s_clientConfig);
        ASSERT_TRUE(result);

        setUserName(newUserName);
    }
} // namespace AwsDocTest
