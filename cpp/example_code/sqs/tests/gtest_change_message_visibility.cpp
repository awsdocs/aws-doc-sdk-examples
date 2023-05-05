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
    TEST_F(SQS_GTests, change_message_visibility_2_) {
        Aws::String queueUrl = getCachedQueueUrl();
        Aws::String messageReceiptHandle = getMessageReceiptHandle();
        ASSERT_FALSE(messageReceiptHandle.empty()) << preconditionError() << std::endl;


        auto result = AwsDoc::SQS::changeMessageVisibility(queueUrl,
                                                           messageReceiptHandle, 4,
                                                           *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
