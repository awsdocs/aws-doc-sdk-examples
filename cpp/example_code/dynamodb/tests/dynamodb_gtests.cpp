/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "dynamodb_gtests.h"
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <fstream>
#include "dynamodb_samples.h"

Aws::SDKOptions AwsDocTest::DynamoDB_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::DynamoDB_GTests::s_clientConfig;
bool AwsDocTest::DynamoDB_GTests::s_ScenarioTableCreated = false;

void AwsDocTest::DynamoDB_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::DynamoDB_GTests::TearDownTestSuite() {

    if (s_ScenarioTableCreated) {
        AwsDoc::DynamoDB::deleteMoviesDynamoDBTable(*s_clientConfig);
        s_ScenarioTableCreated = false;
    }

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
        std::cin.rdbuf(m_savedInBuffer);
        m_savedInBuffer = nullptr;
    }
}

void AwsDocTest::DynamoDB_GTests::AddCommandLineResponses(
        const std::vector<std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}

bool AwsDocTest::DynamoDB_GTests::createTableForScenario() {
    if (!s_ScenarioTableCreated) {
        if (AwsDoc::DynamoDB::createMoviesDynamoDBTable(*s_clientConfig)) {
            s_ScenarioTableCreated = true;
        }
    }

    return s_ScenarioTableCreated;
}

bool AwsDocTest::DynamoDB_GTests::deleteTable(const Aws::String &tableName) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(*s_clientConfig);

    Aws::DynamoDB::Model::DeleteTableRequest request;
    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::DeleteTableOutcome &result = dynamoClient.DeleteTable(
            request);
    if (!result.IsSuccess()) {
        std::cerr << "Failed to delete table: " << result.GetError().GetMessage()
                  << std::endl;
    }

    return result.IsSuccess();
}

