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
    TEST_F(IoT_GTests, getThingShadow_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/get_thing_shadow.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String document;
        result = AwsDoc::IoT::getThingShadow("cpp_test_thing", document,
                                             *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
