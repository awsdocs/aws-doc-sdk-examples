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
#include "iot_samples.h"
#include "iot_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(IoT_GTests, create_topic_rule_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/create_topic_rule.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String ruleName = "cpp_test_rule";
        Aws::String snsTopic = "arn:aws:sns:test:123456789012:gtests_topic";
        Aws::String sql = "SELECT * FROM 'topic/subtopic'";
        Aws::String roleARN = "arn:aws:iam::123456789012:role/service-role/test_iot_role";
        result = AwsDoc::IoT::createTopicRule(ruleName, snsTopic, sql, roleARN,
                                              *s_clientConfig);

        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
