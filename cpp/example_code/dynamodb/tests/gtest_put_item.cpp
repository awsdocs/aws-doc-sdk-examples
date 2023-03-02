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
#include <fstream>
#include "dynamodb_gtests.h"
#include "dynamodb_samples.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(DynamoDB_GTests, put_item_2_) {
        bool result = createSimpleTable();
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::DynamoDB::putItem(SIMPLE_TABLE_NAME, SIMPLE_PRIMARY_KEY,
                                           "value1",
                                           "key2", "value2", "key3", "value3",
                                           "key4", "value4", *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // AwsDocTest