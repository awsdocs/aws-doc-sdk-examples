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
    TEST_F(IoT_GTests, detach_thing_principal_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/empty_response.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String principal = "arn:aws:iot:test:123456789012:cert/24ffa8db4ef58b683d4cf7f65816f15162b2ef8f04f83ec20ea6ae37a2722f1d";
        Aws::String thingName = "cpp_test_thing";

        result = AwsDoc::IoT::detachThingPrincipal(principal, thingName,
                                                   *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
