/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "dynamodb_gtests.h"

namespace AwsDocTest {
    TEST_F(DynamoDB_GTests, getting_started_scenario) {
        printf("test printf\n");

        std::cout << "some text" << std::endl;
        Aws::String string;
        std::cin >> string;
        std::cout <<string << std::endl;
        std::cin >> string;
        std::cout <<string << std::endl;
        std::cin >> string;
        std::cout <<string << std::endl;
    }
} // AwsDocTest