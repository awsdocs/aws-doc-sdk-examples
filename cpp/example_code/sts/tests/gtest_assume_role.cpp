// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <thread>
#include <gtest/gtest.h>
#include <aws/core/auth/AWSCredentialsProvider.h>
#include "sts_samples.h"
#include "sts_gtests.h"

namespace AwsDocTest {
    // This test requires a user. It fails when running in an EC2 instance that assumes a role.
    // Add the 'U' indicating it only runs in a user environment.
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(STS_GTests, assume_role_2U_) {
        Aws::String roleArn = getRoleArn();
        ASSERT_FALSE(roleArn.empty()) << preconditionError() << std::endl;

        Aws::String roleSessionName = uuidName("role-session");
        roleSessionName.resize(std::min(static_cast<size_t>(62), roleSessionName.length()));
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

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(STS_GTests, assume_role_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/AssumeRole.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String roleSessionName = uuidName("role-session");
        roleSessionName.resize(std::min(static_cast<size_t>(62), roleSessionName.length()));
        Aws::String externalId = "012345";	// Optional, but recommended

        Aws::String roleArn = "arn:aws:sts::1111112222222:assumed-role/doc-example-tests-role/doc-example-tests-role-session";

        Aws::Auth::AWSCredentials credentials;

        result = AwsDoc::STS::assumeRole(roleArn, roleSessionName, externalId,
                                              credentials, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
