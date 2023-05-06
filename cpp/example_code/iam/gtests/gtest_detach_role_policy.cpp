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
    TEST_F(IAM_GTests, detach_role_policy_2_) {
        auto roleName = getRole();
        ASSERT_FALSE(roleName.empty()) << preconditionError() << std::endl;
        auto policyArn = samplePolicyARN();

        auto attached = attachRolePolicy(roleName, policyArn);
        ASSERT_TRUE(attached) << preconditionError() << std::endl;

        auto result = AwsDoc::IAM::detachRolePolicy(roleName, policyArn,
                                                    *s_clientConfig);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest
