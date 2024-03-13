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

#include <gtest/gtest.h>
#include "iam_samples.h"
#include "iam_gtests.h"


namespace AwsDocTest {
    // This test requires a user. It fails when running in an EC2 instance that assumes a role.
    // Add the 'U' indicating it only runs in a user environment.
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_user_assume_role_scenario_2U_) {

        auto result = AwsDoc::IAM::iamCreateUserAssumeRoleScenario(*s_clientConfig);
        EXPECT_TRUE(result);
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IAM_GTests, create_user_assume_role_scenario_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/1-CreateUser.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/2-GetUser.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/3-CreateRole.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/4-CreatePolicy.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/5-AssumeRole.xml", Aws::Http::HttpResponseCode::FORBIDDEN);
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/10-AssumeRole.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/ListBucketsFailed.xml", Aws::Http::HttpResponseCode::FORBIDDEN);
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/11-AttachRolePolicy.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/ListBuckets.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/12-DetachRolePolicy.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/13-DeletePolicy.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/14-DeleteRole.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = mockHttp.addResponseWithBody("mock_input/15-DeleteUser.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = AwsDoc::IAM::iamCreateUserAssumeRoleScenario(*s_clientConfig);
        EXPECT_TRUE(result);
    }
} // namespace AwsDocTest