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
#include <filesystem>
#include "sdk_customization_samples.h"
#include "customization_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(SdkCustomization_GTests, custom_response_stream_3_) {
        MockHTTP mockHttp;
        std::vector<std::tuple<std::string, std::string>> headers;
        headers.push_back(std::make_tuple("Content-Type","application/octet-stream"));
        headers.push_back(std::make_tuple("Content-Length","9"));

        bool result = mockHttp.addResponseWithBody(
                "mock_input/CustomResponseStream.txt",
                Aws::Http::HttpResponseCode::OK, headers);
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String testFileName("test-file.txt");
        result = AwsDoc::SdkCustomization::customResponseStream("test-bucket",
                                                                "test-key",
                                                                testFileName,
                                                                *s_clientConfig);
        ASSERT_TRUE(result);
        ASSERT_TRUE(std::filesystem::exists(testFileName.c_str()));

        std::filesystem::remove(testFileName.c_str());
    }
} // namespace AwsDocTest
