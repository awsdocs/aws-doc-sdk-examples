// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"


namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_user_assume_role_scenario) {

        auto result = AwsDoc::IAM::iamCreateUserAssumeRoleScenario(*s_clientConfig);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest