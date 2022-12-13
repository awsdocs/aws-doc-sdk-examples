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
    TEST_F(DynamoDB_GTests, create_table) {

        Aws::String tableName = uuidName("table");

        bool result = AwsDoc::DynamoDB::createDynamoDBTable(tableName,
                                                            "primary_key",
                                                            *s_clientConfig);
        ASSERT_TRUE(result);

        deleteTable(tableName);
}
} // A