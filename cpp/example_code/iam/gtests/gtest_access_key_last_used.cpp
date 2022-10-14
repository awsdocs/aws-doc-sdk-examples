/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, access_key_last_used) {
        auto keyID = getExistingKey();
        ASSERT_FALSE(keyID.empty()) << preconditionError() << std::endl;

        auto result = AwsDoc::IAM::accessKeyLastUsed(keyID, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
