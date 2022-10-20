/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_policy) {
        Aws::String policyName = uuidName("policy");
        auto policyArn = AwsDoc::IAM::createPolicy(policyName, "arn:aws:s3:::*",
                                                   *s_clientConfig);
        ASSERT_FALSE(policyArn.empty());

        deletePolicy(policyArn);
    }
} // namespace AwsDocTest
