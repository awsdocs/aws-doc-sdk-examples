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
    TEST_F(SQS_GTests, create_queue_2_) {
        Aws::String queueName = uuidName("test-queue");

        auto result = AwsDoc::SQS::createQueue(queueName, *s_clientConfig);
        ASSERT_TRUE(result);

        deleteQueueWithName(queueName);
    }

} // namespace AwsDocTest
