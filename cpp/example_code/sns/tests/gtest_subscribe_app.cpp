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
    TEST_F(SNS_GTests, subscribe_app_3_) {

        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/subscribe_app.xml");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String topicARN = "arn:aws:sns:us-test:123456789012:MyTopic";
        Aws::String endpointARN = "arn:aws:sns:test:123456789012:endpoint/GCM/gcmpushapp/5e3e9847-3183-3f18-a7e8-671c3a57d4b3";
        result = AwsDoc::SNS::subscribeApp(topicARN, endpointARN, *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
