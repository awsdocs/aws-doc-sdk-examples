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
#include "sns_samples.h"
#include "sns_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(SNS_GTests, subscribe_lambda_3_) {

        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/subscribe_lambda.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String topicARN = "arn:aws:sns:us-test:123456789012:MyTopic";
        Aws::String lambdaARN = "arn:aws:sns:us-test:123456789012:function:hello_sns";

        result = AwsDoc::SNS::subscribeLambda(topicARN, lambdaARN, *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
