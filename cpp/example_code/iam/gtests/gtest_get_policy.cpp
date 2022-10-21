/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, get_policy) {
        auto policyArn = getPolicy();  // Creates policy if necessary.
        ASSERT_FALSE(policyArn.empty()) << preconditionError() << std::endl;

        auto result = AwsDoc::IAM::getPolicy(policyArn, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
