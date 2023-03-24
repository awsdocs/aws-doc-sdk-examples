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
#include "sns_samples.h"
#include "sns_gtests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(SNS_GTests, delete_topic_2_) {
        Aws::String topicARN;
        bool result = createTopic(topicARN);
        ASSERT_TRUE(result) << preconditionError() << std::endl;

        result = AwsDoc::SNS::deleteTopic(topicARN, *s_clientConfig);
        ASSERT_TRUE(result);
    }

} // namespace AwsDocTest
