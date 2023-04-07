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

#include "MediaConvert_gtests.h"
#include "mediaconvert_samples.h"
#include <gtest/gtest.h>

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(MediaConvert_GTests, create_job_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/createJob.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String roleARN = "arn:aws:iam::123456789:role/media_convert_test";
        Aws::String fileInput = "s3://test-bucket/test.mp4";
        Aws::String fileOutput = "s3://test-bucket/output_1";

        result = AwsDoc::MediaConvert::createJob(roleARN, fileInput, fileOutput, "",
                                                 *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
