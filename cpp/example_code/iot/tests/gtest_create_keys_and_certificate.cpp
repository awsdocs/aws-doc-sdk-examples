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
    TEST_F(IoT_GTests, create_keys_and_certificate_no_output_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody(
                "mock_input/create_keys_and_certificate.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String certificateARN;
        Aws::String certificateID;
        result = AwsDoc::IoT::createKeysAndCertificate("", certificateARN,
                                                       certificateID, *s_clientConfig);
        ASSERT_TRUE(result);
    }

    TEST_F(IoT_GTests, create_keys_and_certificate_with_output_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody(
                "mock_input/create_keys_and_certificate.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String certificateARN;
        Aws::String certificateID;
        result = AwsDoc::IoT::createKeysAndCertificate(TEST_OUT_DIR, certificateARN,
                                                       certificateID, *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
