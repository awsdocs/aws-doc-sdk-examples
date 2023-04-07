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

#include <gtest/gtest.h>
#include "mediaconvert_samples.h"
#include "MediaConvert_gtests.h"

namespace AwsDocTest {
    TEST_F(MediaConvert_GTests, get_job_3_) {
        MockHTTP mockHttp;
        bool result = mockHttp.addResponseWithBody("mock_input/createJob.json");
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        Aws::String jobID = "1234567890123-abcdef";

        result = AwsDoc::MediaConvert::getJob(jobID, *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
