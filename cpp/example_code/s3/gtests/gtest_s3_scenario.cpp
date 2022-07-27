//
// Created by Meyer, Steve on 7/27/22.
//

#include <gtest/gtest.h>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <fstream>

bool S3Scenario(const Aws::String& key, const Aws::String& objectPath,
                                          const Aws::String& savePath, const Aws::String& toFolder,
                                          const Aws::Client::ClientConfiguration &clientConfig);


// Demonstrate some basic assertions.
TEST(S3ScenarioTest, TEST_FUNCTION) {
    // Expect two strings not to be equal.
    Aws::SDKOptions options;
    InitAPI(options);
    Aws::Client::ClientConfiguration clientConfig;

    const char* TEST_FILE = "test.txt";
    const char* TEST_SAVE_FILE = "test2.txt";
    {
        std::ofstream file_stream(TEST_FILE);
        file_stream << "some text" << std::endl;
    }

    EXPECT_TRUE(S3Scenario("test_key", TEST_FILE, TEST_SAVE_FILE, "test_folder", clientConfig));
    
    ShutdownAPI(options);
}