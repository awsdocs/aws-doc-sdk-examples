/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "dynamodb_gtests.h"
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <aws/dynamodb/model/DeleteItemRequest.h>
#include <aws/dynamodb/model/PutItemRequest.h>
#include <aws/core/utils/UUID.h>
#include <fstream>
#include "dynamodb_samples.h"

namespace AwsDocTest {
    Aws::SDKOptions DynamoDB_GTests::s_options;
    std::unique_ptr<Aws::Client::ClientConfiguration> DynamoDB_GTests::s_clientConfig;
    bool DynamoDB_GTests::s_ScenarioTableCreated = false;

    static Aws::String FORUM_TABLE_NAME("Forum"); // Must match sample data.
    static Aws::String PRODUCT_CATALOG_TABLE_NAME(
            "ProductCatalog"); // Must match sample data.

    const Aws::String DynamoDB_GTests::SIMPLE_TABLE_NAME("aws_doc_example_cpp_test");
    const Aws::String DynamoDB_GTests::SIMPLE_PRIMARY_KEY("primary_key");

    bool DynamoDB_GTests::s_SimpleTableCreated = false;
    bool DynamoDB_GTests::s_BatchTablesCreated = false;
    bool DynamoDB_GTests::s_batchTablesPopulated = false;
}

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

    if (s_SimpleTableCreated) {
        deleteTable(SIMPLE_TABLE_NAME);
        s_SimpleTableCreated = false;
    }

    if (s_BatchTablesCreated) {
        deleteBatchGetItemTables();
        s_BatchTablesCreated = false;
    }

    ShutdownAPI(s_options);
}

void AwsDocTest::DynamoDB_GTests::SetUp() {
    m_savedOutBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);

    // The following code is needed for the AwsDocTest::MyStringBuffer::underflow exception.
    // Otherwise, we get an infinite loop when the buffer is empty.
    std::cin.exceptions(std::ios_base::badbit);
}

void AwsDocTest::DynamoDB_GTests::TearDown() {
    if (m_savedOutBuffer != nullptr) {
        std::cout.rdbuf(m_savedOutBuffer);
        m_savedOutBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cin.rdbuf(m_savedInBuffer);
        std::cin.exceptions(std::ios_base::goodbit);
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

bool AwsDocTest::DynamoDB_GTests::createSimpleTable() {
    if (!s_SimpleTableCreated) {
        s_SimpleTableCreated = createTable(SIMPLE_TABLE_NAME, SIMPLE_PRIMARY_KEY,
                                           Aws::DynamoDB::Model::ScalarAttributeType::S);
    }

    return s_SimpleTableCreated;
}

bool AwsDocTest::DynamoDB_GTests::createTable(const Aws::String &tableName,
                                              const Aws::String &partitionKey,
                                              Aws::DynamoDB::Model::ScalarAttributeType type) {
    bool result = false;
    Aws::DynamoDB::DynamoDBClient dynamoClient(*s_clientConfig);
    Aws::DynamoDB::Model::CreateTableRequest request;

    Aws::DynamoDB::Model::AttributeDefinition hashKey;
    hashKey.SetAttributeName(partitionKey);
    hashKey.SetAttributeType(type);
    request.AddAttributeDefinitions(hashKey);

    Aws::DynamoDB::Model::KeySchemaElement keySchemaElement;
    keySchemaElement.WithAttributeName(partitionKey).WithKeyType(
            Aws::DynamoDB::Model::KeyType::HASH);
    request.AddKeySchema(keySchemaElement);

    Aws::DynamoDB::Model::ProvisionedThroughput throughput;
    throughput.WithReadCapacityUnits(5).WithWriteCapacityUnits(5);
    request.SetProvisionedThroughput(throughput);
    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::CreateTableOutcome &outcome = dynamoClient.CreateTable(
            request);

    if (outcome.IsSuccess()) {
        result = AwsDoc::DynamoDB::waitTableActive(tableName, *s_clientConfig);
    }
    else if (outcome.GetError().GetErrorType() ==
             Aws::DynamoDB::DynamoDBErrors::RESOURCE_IN_USE) {
        result = true; // Table already exists.
    }
    else {
        std::cerr << "Failed to create table: " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}

bool AwsDocTest::DynamoDB_GTests::createBatchGetItemTables() {
    if (!s_BatchTablesCreated) {
        if (createTable(PRODUCT_CATALOG_TABLE_NAME, "Id",
                        Aws::DynamoDB::Model::ScalarAttributeType::N)) {
            s_BatchTablesCreated = createTable(FORUM_TABLE_NAME, "Name",
                                               Aws::DynamoDB::Model::ScalarAttributeType::S);
        }
    }

    return s_BatchTablesCreated;
}

bool AwsDocTest::DynamoDB_GTests::deleteBatchGetItemTables() {
    bool result = deleteTable(PRODUCT_CATALOG_TABLE_NAME);
    return result && deleteTable(FORUM_TABLE_NAME);
}

Aws::String AwsDocTest::DynamoDB_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

bool AwsDocTest::DynamoDB_GTests::populateBatchTables() {
    if (!s_batchTablesPopulated) {
        if (AwsDoc::DynamoDB::batchWriteItem(TESTS_DIR "/ProductCatalog.json",
                                             *s_clientConfig)) {
            s_batchTablesPopulated = AwsDoc::DynamoDB::batchWriteItem(
                    TESTS_DIR "/Forum.json",
                    *s_clientConfig);;
        }
    }

    return s_batchTablesPopulated;
}

Aws::String AwsDocTest::DynamoDB_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return "doc-example-tests-" + name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

bool AwsDocTest::DynamoDB_GTests::putItem(const Aws::String &tableName,
                                          const std::vector<Aws::String> &keys,
                                          const std::vector<Aws::String> &values) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(*s_clientConfig);

    Aws::DynamoDB::Model::PutItemRequest putItemRequest;
    putItemRequest.SetTableName(tableName);

    size_t count = std::min(keys.size(), values.size());
    for (size_t i = 0; i < count; ++i) {
        putItemRequest.AddItem(keys[i], Aws::DynamoDB::Model::AttributeValue().SetS(
                values[i]));
    }

    const Aws::DynamoDB::Model::PutItemOutcome outcome = dynamoClient.PutItem(
            putItemRequest);
    if (!outcome.IsSuccess()) {
        std::cerr << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDocTest::DynamoDB_GTests::deleteItem(const Aws::String &tableName,
                                             const std::vector<Aws::String> &keys,
                                             const std::vector<Aws::String> &values) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(*s_clientConfig);

    Aws::DynamoDB::Model::DeleteItemRequest request;

    size_t count = std::min(keys.size(), values.size());
    for (size_t i = 0; i < count; ++i) {
        request.AddKey(keys[i], Aws::DynamoDB::Model::AttributeValue().SetS(
                values[i]));
    }
    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::DeleteItemOutcome &outcome = dynamoClient.DeleteItem(
            request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to delete item: " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}

