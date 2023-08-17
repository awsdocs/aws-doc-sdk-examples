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
    TEST_F(IAM_GTests, create_role_2_) {
        Aws::String roleName = uuidName("role");
        auto result = AwsDoc::IAM::createIamRole(roleName, getAssumeRolePolicyJSON(),
                                                 *s_clientConfig);
        ASSERT_TRUE(result);

        deleteRole(roleName);
    }
} // namespace AwsDocTest
