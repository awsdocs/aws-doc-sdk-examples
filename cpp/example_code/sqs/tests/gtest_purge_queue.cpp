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
#include "sqs_samples.h"
#include "sqs_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(SQS_GTests, purge_queue_2_) {
        Aws::String queueUrl = createQueue("purge_test");
        ASSERT_FALSE(queueUrl.empty()) << preconditionError() << std::endl;

        auto result = AwsDoc::SQS::purgeQueue(queueUrl, *s_clientConfig);
        EXPECT_TRUE(result);

        deleteQueueWithUrl(queueUrl);
    }

} // namespace AwsDocTest
