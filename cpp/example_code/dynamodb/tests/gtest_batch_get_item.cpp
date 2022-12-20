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
    TEST_F(DynamoDB_GTests, batch_get_item) {
        bool result = createBatchGetItemTables();
        ASSERT_TRUE(result) << preconditionError();

        result = populateBatchTables();
        ASSERT_TRUE(result) << preconditionError();

        result = AwsDoc::DynamoDB::batchGetItem(*s_clientConfig);
        ASSERT_TRUE(result);
    }
} // AwsDocTest