/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "lambda_gtests.h"
#include "lambda_samples.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(Lambda_GTests, get_started_with_functions_scenario) {
        AddCommandLineResponses({"8",
                                 "",
                                 "1",
                                 "4",
                                 "8",
                                 "y",
                                 "2",
                                 "456",
                                 "231",
                                 "y",
                                 "3",
                                 "34",
                                 "6789",
                                 "y",
                                 "4",
                                 "81",
                                 "9",
                                 "n",
                                 "",
                                 ""});


        bool result = AwsDoc::Lambda::getStartedWithFunctionsScenario(*s_clientConfig);
        ASSERT_TRUE(result);

        std::stringstream output(m_coutBuffer.str());

        bool hasIncrementResult = false;
        std::string incrementResult("9");
        while (output) {
            std::string line;
            std::getline(output, line);
            if (line.find(AwsDoc::Lambda::INCREMENT_RESUlT_PREFIX) !=
                std::string::npos) {
                if (line.rfind(incrementResult) ==
                    line.size() - incrementResult.length()) {
                    hasIncrementResult = true;
                }
                break;
            }
        }
        ASSERT_TRUE(hasIncrementResult);

        int operationsCorrect = 0;
        std::vector<std::string> operationResults = {"12", "225", "230826", "9"};

        output.seekg(0);
        while (output && (operationsCorrect < operationResults.size())) {
            std::string line;
            std::getline(output, line);
            if (line.find(AwsDoc::Lambda::ARITHMETIC_RESUlT_PREFIX) !=
                std::string::npos) {
                if (line.rfind(operationResults[operationsCorrect]) ==
                    line.size() - operationResults[operationsCorrect].length()) {
                    operationsCorrect++;
                }
                else {
                    break;
                }
            }
        }

        ASSERT_EQ(operationsCorrect, operationResults.size());
    }
} // AwsDocTest