/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
#include <gtest/gtest.h>
#include <fstream>
#include "dynamodb_gtests.h"
#include "dynamodb_samples.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(DynamoDB_GTests, get_item) {
        bool result = createSimpleTable();
        ASSERT_TRUE(result) << preconditionError();

        const std::vector<Aws::String> keys = {SIMPLE_PRIMARY_KEY, "second_key"};
        const std::vector<Aws::String> values = {"primary_value", "second_value"};
        result = putItem(SIMPLE_TABLE_NAME, keys, values);
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::DynamoDB::getItem(SIMPLE_TABLE_NAME,
                                           keys[0],
                                           values[0],
                                           *s_clientConfig);
        EXPECT_TRUE(result);

        deleteItem(SIMPLE_TABLE_NAME,
                   std::vector<Aws::String>(keys.begin(), keys.begin() + 1),
                   std::vector<Aws::String>(values.begin(), values.begin() + 1));
    }
} // AwsDocTest