/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "glue_gtests.h"
#include <fstream>

Aws::SDKOptions AwsDocTest::Glue_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::Glue_GTests::s_clientConfig;

void AwsDocTest::Glue_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::Glue_GTests::TearDownTestSuite() {
     ShutdownAPI(s_options);

}

void AwsDocTest::Glue_GTests::SetUp() {
    m_savedBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);
}

void AwsDocTest::Glue_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cin.rdbuf(m_savedInBuffer);
        m_savedInBuffer = nullptr;
    }
}

Aws::String AwsDocTest::Glue_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::Glue_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

