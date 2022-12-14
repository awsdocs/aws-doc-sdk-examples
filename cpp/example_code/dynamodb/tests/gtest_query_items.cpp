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
    TEST_F(DynamoDB_GTests, query_items) {
        bool result = createSimpleTable();
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::DynamoDB::queryItems(SIMPLE_TABLE_NAME, SIMPLE_PRIMARY_KEY,
                                              "value1",
                                              "", *s_clientConfig);
        ASSERT_TRUE(result);
    }
} // AwsDocTest