/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "lambda_gtests.h"
#include <aws/core/client/ClientConfiguration.h>

Aws::SDKOptions AwsDocTest::Lambda_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::Lambda_GTests::s_clientConfig;

void AwsDocTest::Lambda_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::Lambda_GTests::TearDownTestSuite() {
    ShutdownAPI(s_options);

}

void AwsDocTest::Lambda_GTests::SetUp() {
    m_savedBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);
}

void AwsDocTest::Lambda_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cin.rdbuf(m_savedInBuffer);
        m_savedInBuffer = nullptr;
    }
}

Aws::String AwsDocTest::Lambda_GTests::preconditionError() {
    return "Failed to meet precondition.";
}


void AwsDocTest::Lambda_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

bool AwsDocTest::Lambda_GTests::getTrailingInt(const std::string &string, int &result) {
    size_t index = string.length() - 1;
    while (string.length() <= index && std::isdigit(string[index])) {
        --index;
    }

    if (index < (string.length() - 1)) {
        result = std::stoi(string.substr(index + 1));
        return true;
    }

    return false;
}
