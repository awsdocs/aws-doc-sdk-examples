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
#include <fstream>
#include <cstdio>

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(MedicalImaging_GTests, get_image_set_metadata_without_version_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody(
                "mock_input/deleteImageSet.json.gz");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String outFileName = "test.json.gzip";
        result = AwsDoc::Medical_Imaging::getImageSetMetadata(
                "12345678901234567890123456789012",
                "12345678901234567890123456789012",
                "",
                outFileName, *s_clientConfig);
        ASSERT_TRUE(result);

        std::ifstream ifs(outFileName);
        ASSERT_TRUE(ifs);
        std::remove(outFileName.c_str());
    }

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(MedicalImaging_GTests, get_image_set_metadata_with_version_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody(
                "mock_input/deleteImageSet.json.gz");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String outFileName = "test.json.gzip";
        result = AwsDoc::Medical_Imaging::getImageSetMetadata(
                "12345678901234567890123456789012",
                "12345678901234567890123456789012",
                "1",
                outFileName, *s_clientConfig);
        ASSERT_TRUE(result);

        std::ifstream ifs(outFileName);
        ASSERT_TRUE(ifs);
        std::remove(outFileName.c_str());
    }

} // namespace AwsDocTest
