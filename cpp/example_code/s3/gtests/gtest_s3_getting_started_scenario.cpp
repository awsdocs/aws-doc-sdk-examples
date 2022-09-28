// NOLINTNEXTLINE(readability-named-parameter)
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <gtest/gtest.h>
#include <fstream>
#include "awsdoc/s3/s3_examples.h"
#include "S3_GTests.h"

namespace AwsDocTest {
// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_getting_started_scenario) {

        Aws::String testFile = GetTestFilePath();
        ASSERT_TRUE(!testFile.empty()) << "Failed precondition  for test." << std::endl;

        const char *TEST_SAVE_FILE = "test2.txt";

        EXPECT_TRUE(AwsDoc::S3::S3_GettingStartedScenario(testFile, TEST_SAVE_FILE, *s_clientConfig));

        {
            std::ifstream save_file(TEST_SAVE_FILE);
            EXPECT_TRUE(save_file.is_open());
        }

        remove(TEST_SAVE_FILE);
    }
} // namespace AwsDocTest