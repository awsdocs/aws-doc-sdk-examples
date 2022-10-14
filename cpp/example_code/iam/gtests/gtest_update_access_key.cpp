/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, update_access_key) {
        auto key = getExistingKey();
        ASSERT_FALSE(key.empty()) << preconditionError() << std::endl;

        auto userName = getUser();
        ASSERT_FALSE(key.empty()) << preconditionError() << std::endl;

        auto result = AwsDoc::IAM::updateAccessKey(userName, key,
                                                   Aws::IAM::Model::StatusType::Inactive,
                                                   *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
