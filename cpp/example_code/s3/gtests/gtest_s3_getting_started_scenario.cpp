//
// Created by Meyer, Steve on 7/27/22.
//

#include <gtest/gtest.h>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <fstream>
#include "awsdoc/s3/s3_examples.h"


TEST(S3ScenarioTest, Test_valid_arguments) {
    Aws::SDKOptions options;
    InitAPI(options);
    Aws::Client::ClientConfiguration clientConfig;

    const char* TEST_FILE = "test.txt";
    const char* TEST_SAVE_FILE = "test2.txt";

    {
        std::ofstream file_stream(TEST_FILE);
        file_stream << "some text" << std::endl;
    }

    EXPECT_TRUE(AwsDoc::S3::S3_GettingStartedScenario(TEST_FILE, TEST_SAVE_FILE, clientConfig, false));

    {
        std::ifstream save_file(TEST_SAVE_FILE);
        EXPECT_TRUE(save_file.is_open());
    }

    ShutdownAPI(options);
}