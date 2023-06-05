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
#include "../sqs_samples.h"
#include "sqs_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(SQS_GTests, dead_letter_queue_2_) {
        Aws::String srcQueueUrl = createQueue(uuidName("dead_source"));
        ASSERT_FALSE(srcQueueUrl.empty()) << preconditionError() << std::endl;

        Aws::String deadLetterQueueUrl = createQueue("dead_dest");
        ASSERT_FALSE(deadLetterQueueUrl.empty()) << preconditionError() << std::endl;

        Aws::String deadLetterQueueArn = getQueueArn(deadLetterQueueUrl);
        EXPECT_FALSE(deadLetterQueueArn.empty()) << preconditionError() << std::endl;

        if (!deadLetterQueueArn.empty()) {
            auto result = AwsDoc::SQS::setDeadLetterQueue(srcQueueUrl,
                                                          deadLetterQueueArn, 4,
                                                          *s_clientConfig);
            EXPECT_TRUE(result);
        }

        deleteQueueWithUrl(srcQueueUrl);
        deleteQueueWithUrl(deadLetterQueueUrl);
    }

} // namespace AwsDocTest
