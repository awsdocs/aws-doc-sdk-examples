/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "dynamodb_gtests.h"
#include <fstream>

Aws::SDKOptions AwsDocTest::DynamoDB_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::DynamoDB_GTests::s_clientConfig;

void AwsDocTest::DynamoDB_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::DynamoDB_GTests::TearDownTestSuite() {

    ShutdownAPI(s_options);

}

void AwsDocTest::DynamoDB_GTests::SetUp() {
    m_savedOutBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);
}

void AwsDocTest::DynamoDB_GTests::TearDown() {
    if (m_savedOutBuffer != nullptr) {
        std::cout.rdbuf(m_savedOutBuffer);
        m_savedOutBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cout.rdbuf(m_savedInBuffer);
        m_savedInBuffer = nullptr;
    }
}

void AwsDocTest::DynamoDB_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto& response : responses)
    {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

