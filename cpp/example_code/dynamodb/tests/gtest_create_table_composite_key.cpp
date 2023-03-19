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
    TEST_F(DynamoDB_GTests, create_table_composite_key_2_) {

        Aws::String tableName = uuidName("table");

        bool result = AwsDoc::DynamoDB::createTableWithCompositeKey(tableName,
                                                                    "partition_key",
                                                                    "sort_key",
                                                                    *s_clientConfig);
        ASSERT_TRUE(result);

        deleteTable(tableName);
    }
} // AwsDocTest