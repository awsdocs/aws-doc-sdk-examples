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
#include "rekognition_samples.h"
#include "rekognition_gtests.h"

namespace AwsDocTest {
    // Designated _2R_ because it requires resources.
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(Rekognition_GTests, detect_labels_2R_) {
        Aws::String bucketName = getImageBucket();
        ASSERT_FALSE(bucketName.empty()) << preconditionError() << std::endl;
        Aws::String fileName = getImageFileName();
        Aws::String imageKey = "test_rekognition_cpp.jpg";

        bool result = uploadImage(bucketName, fileName, imageKey);
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = AwsDoc::Rekognition::detectLabels(bucketName, imageKey, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
