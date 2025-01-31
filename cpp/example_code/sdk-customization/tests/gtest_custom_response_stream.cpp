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
    TEST_F(SdkCustomization_GTests, custom_response_stream_2_) {
        Aws::String bucketName = uuidName("cpp-test-customization");
        Aws::String keyName = uuidName("custom-response");
        bool result = createBucket(bucketName);
        ASSERT_TRUE(result) << preconditionError() << std::endl;
        result = putFileInBucket(bucketName, keyName, getTestFilePath());
        ASSERT_TRUE(result) << preconditionError() << std::endl;
        Aws::String testFileName("test-file.txt");
        result = AwsDoc::SdkCustomization::customResponseStream(bucketName,
                                                                keyName,
                                                                testFileName,
                                                                *s_clientConfig);

        EXPECT_TRUE(result);

        EXPECT_TRUE(std::filesystem::exists(testFileName.c_str()));

        std::filesystem::remove(testFileName.c_str());

        result = deleteObjectInBucket(bucketName, keyName);
        EXPECT_TRUE(result) << "custom_response_stream_2_ cleanup" << std::endl;

        result = deleteBucket(bucketName);
        EXPECT_TRUE(result) << "custom_response_stream_2_ cleanup" << std::endl;
    }
} // namespace AwsDocTest
