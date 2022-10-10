/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <aws/core/utils/UUID.h>
#include "iam_samples.h"
#include "iam_gtests.h"

namespace AwsDocTest { 
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_account_alias) {
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String aliasName = "doc-example-tests-alias-" +
                               Aws::Utils::StringUtils::ToLower(uuid.c_str());

        auto result = AwsDoc::IAM::createAccountAlias(aliasName, *s_clientConfig);
        ASSERT_TRUE(result);

        deleteAccountAlias(aliasName);
    }
} // namespace AwsDocTest
