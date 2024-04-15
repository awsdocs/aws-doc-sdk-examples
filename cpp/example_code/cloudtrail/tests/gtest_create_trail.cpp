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
#include "cloudtrail_samples.h"
#include "cloudtrail_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(CloudTrail_GTests, create_trail_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/CreateTrail.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String trailName = "test-trail";
        Aws::String bucketName = "example-bucket";
        result = AwsDoc::CloudTrail::createTrail(trailName, bucketName,
                                                 *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
