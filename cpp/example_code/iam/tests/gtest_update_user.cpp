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
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, update_user_2_) {
        auto user = getUser();
        ASSERT_FALSE(user.empty());
        auto newUserName = uuidName("user");

        auto result = AwsDoc::IAM::updateUser(user, newUserName, *s_clientConfig);
        ASSERT_TRUE(result);

        setUserName(newUserName);
    }
} // namespace AwsDocTest
