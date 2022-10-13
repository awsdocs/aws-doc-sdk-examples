/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <aws/core/auth/AWSCredentialsProvider.h>
#include "sts_samples.h"
#include "sts_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(STS_GTests, assume_role) {
        Aws::String roleArn = getRoleArn();
        ASSERT_FALSE(roleArn.empty()) << preconditionError() << std::endl;

        Aws::String roleSessionName = uuidName("role-session");
        roleSessionName.resize(std::min(62ul, roleSessionName.length()));
        Aws::String externalId = "012345";	// Optional, but recommended
        Aws::Auth::AWSCredentials credentials;

        // Make multiple attempts until role is available to assume.
        bool result = false;
        for (int i = 0; i < 20; ++i) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
            if (AwsDoc::STS::assumeRole(roleArn, roleSessionName, externalId,
                                                  credentials, *s_clientConfig))
            {
                result = true;
                break;
            }
        }
        ASSERT_TRUE(result);
}
} // namespace AwsDocTest
