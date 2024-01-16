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
#include "medical-imaging_samples.h"
#include "medical-imaging_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(MedicalImaging_GTests, delete_image_set_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/deleteImageSet.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = AwsDoc::Medical_Imaging::deleteImageSet(
                "12345678901234567890123456789012",
                "12345678901234567890123456789012",
                *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
