/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


#include <gtest/gtest.h>
#include <fstream>
#include "dynamodb_gtests.h"
#include "dyanamodb_samples.h"

namespace AwsDocTest {

    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(DynamoDB_GTests, paritql_single_execute_scenario) {
        AddCommandLineResponses({"Jaws",
                                 "1972",
                                 "8",
                                 "Sharks",
                                 "7",
                                 "Sharks and Dude",
                                 "y",
                                 "2011",
                                 "2011",
                                 "2018",
                                 "30",
                                 "y"});
        bool result = createTable();
        ASSERT_TRUE(result);

        result = AwsDoc::DynamoDB::partiqlExecuteScenario(*s_clientConfig);
        ASSERT_TRUE(result);
    }


    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(DynamoDB_GTests, partiql_batch_execute_scenario) {
        AddCommandLineResponses({"Godzilla",
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

        bool result = createTable();
        ASSERT_TRUE(result);

        result = AwsDoc::DynamoDB::partiqlBatchExecuteScenario(*s_clientConfig);
        ASSERT_TRUE(result);
    }
} // AwsDocTest