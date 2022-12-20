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
    TEST_F(DynamoDB_GTests, partiql_batch_execute_scenario
    ) {
        AddCommandLineResponses({
                                        "Godzilla",
                                        "1972",
                                        "8",
                                        "monster",
                                        "y",
                                        "Poseidon Adventure",
                                        "1973",
                                        "7",
                                        "Boat",
                                        "y",
                                        "Star Wars",
                                        "1978",
                                        "9",
                                        "Troopers",
                                        "n",
                                        "3",
                                        "4",
                                        "5"});

        bool result = createTableForScenario();
        ASSERT_TRUE(result);

        result = AwsDoc::DynamoDB::partiqlBatchExecuteScenario(*s_clientConfig);
        ASSERT_TRUE(result);
    }
} // AwsDocTest