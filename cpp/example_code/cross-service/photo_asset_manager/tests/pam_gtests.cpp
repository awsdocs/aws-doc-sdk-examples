// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include "pam_gtests.h"
#include <fstream>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/utils/UUID.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <aws/dynamodb/model/DeleteItemRequest.h>
#include <aws/dynamodb/model/BatchWriteItemRequest.h>
#include <aws/dynamodb/model/DescribeTableRequest.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include "cpp_lambda_functions.h"

Aws::SDKOptions AwsDocTest::PAM_GTests::s_options;
std::unique_ptr <Aws::Client::ClientConfiguration> AwsDocTest::PAM_GTests::s_clientConfig;
std::string AwsDocTest::PAM_GTests::s_cachedDynamoDBTable;
std::string AwsDocTest::PAM_GTests::s_cachedS3Bucket;

void AwsDocTest::PAM_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // s_clientConfig must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::PAM_GTests::TearDownTestSuite() {
    if (!s_cachedS3Bucket.empty()) {
        DeleteBucket(s_cachedS3Bucket);
        s_cachedS3Bucket.clear();
    }

    if (!s_cachedDynamoDBTable.empty()) {
        deleteTable(s_cachedDynamoDBTable);
        s_cachedDynamoDBTable.clear();
    }

    ShutdownAPI(s_options);
}

void AwsDocTest::PAM_GTests::SetUp() {
    if (suppressStdOut()) {
        m_savedBuffer = std::cout.rdbuf();
        std::cout.rdbuf(&m_coutBuffer);
    }

    m_savedInBuffer = std::cin.rdbuf();
    std::cin.rdbuf(&m_cinBuffer);

    // The following code is needed for the AwsDocTest::MyStringBuffer::underflow exception.
    // Otherwise, an infinite loop occurs when looping for a result on an empty buffer.
    std::cin.exceptions(std::ios_base::badbit);
}

void AwsDocTest::PAM_GTests::TearDown() {

    if (!m_databaseName.empty() && !m_labelsToDelete.empty()) {
        deleteLabelsInTable(m_databaseName, m_labelsToDelete);
        m_databaseName.clear();
        m_labelsToDelete.clear();
    }

    for (auto &pair: m_ObjectsToDelete) {
        deleteObjectInBucket(pair.first, pair.second);
    }
    m_ObjectsToDelete.clear();

    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }

    if (m_savedInBuffer != nullptr) {
        std::cin.rdbuf(m_savedInBuffer);
        std::cin.exceptions(std::ios_base::goodbit);
        m_savedInBuffer = nullptr;
    }
}

Aws::String AwsDocTest::PAM_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

void AwsDocTest::PAM_GTests::AddCommandLineResponses(
        const std::vector <std::string> &responses) {

    std::stringstream stringStream;
    for (auto &response: responses) {
        stringStream << response << "\n";
    }
    m_cinBuffer.str(stringStream.str());
}


bool AwsDocTest::PAM_GTests::suppressStdOut() {
    return std::getenv("EXAMPLE_TESTS_LOG_ON") == nullptr;
}

Aws::String AwsDocTest::PAM_GTests::uuidName(const Aws::String &prefix) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return prefix + Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

bool AwsDocTest::PAM_GTests::deleteLabelsInTable(const std::string &databaseName,
                                                 const std::vector <std::string> &labels) {
    Aws::DynamoDB::DynamoDBClient dbClient(*s_clientConfig);
    Aws::Vector <Aws::DynamoDB::Model::WriteRequest> writeRequests;
    for (const auto &label: labels) {
        Aws::Map <Aws::String, Aws::DynamoDB::Model::AttributeValue> attributes;
        attributes[AwsDoc::PAM::LABEL_KEY] = Aws::DynamoDB::Model::AttributeValue().SetS(
                label);
        Aws::DynamoDB::Model::DeleteRequest deleteItemRequest;
        deleteItemRequest.SetKey(attributes);
        writeRequests.push_back(Aws::DynamoDB::Model::WriteRequest().WithDeleteRequest(
                deleteItemRequest));
    }

    Aws::DynamoDB::Model::BatchWriteItemRequest batchWriteItemRequest;
    batchWriteItemRequest.AddRequestItems(databaseName, writeRequests);

    Aws::DynamoDB::Model::BatchWriteItemOutcome outcome = dbClient.BatchWriteItem(
            batchWriteItemRequest);

    if (outcome.IsSuccess()) {
        std::cout << "DynamoDB::BatchWriteItem was successful." << std::endl;
    }
    else {
        std::cerr << "Error with DynamoDB::BatchWriteItem. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDocTest::PAM_GTests::deleteObjectInBucket(const std::string &bucketName,
                                                  const std::string &objectName) {
    Aws::S3::S3Client s3Client(*s_clientConfig);

    Aws::S3::Model::DeleteObjectRequest deleteObjectRequest;
    deleteObjectRequest.SetBucket(bucketName);
    deleteObjectRequest.SetKey(objectName);
    auto outcome = s3Client.DeleteObject(deleteObjectRequest);
    if (!outcome.IsSuccess()) {
        std::cerr << "AwsDocTest::PAM_GTests::deleteObjectInBucket failed with error. "
                  << outcome.GetError().GetMessage() << std::endl;
    }
    return outcome.IsSuccess();
}

void AwsDocTest::PAM_GTests::setDatabaseName(const std::string &databaseName) {
    m_databaseName = databaseName;
}

void AwsDocTest::PAM_GTests::addLabelsToDelete(
        const std::vector <std::string> &labelsToDelete) {
    m_labelsToDelete.insert(m_labelsToDelete.cend(), labelsToDelete.cbegin(),
                            labelsToDelete.cend());
}

void AwsDocTest::PAM_GTests::addObjectToDelete(const std::string &bucket,
                                               const std::string &key) {
    m_ObjectsToDelete.emplace_back(bucket, key);
}

bool AwsDocTest::PAM_GTests::DeleteBucket(const Aws::String &bucketName) {
    Aws::S3::S3Client client(*s_clientConfig);

    Aws::S3::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketOutcome outcome =
            client.DeleteBucket(request);

    bool result = true;
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cout << "S3_GTests::DeleteBucket Error: DeleteBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}

bool AwsDocTest::PAM_GTests::CreateBucket(const Aws::String &bucketName) {
    Aws::S3::S3Client client(*s_clientConfig);
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);
    request.SetObjectOwnership(
            Aws::S3::Model::ObjectOwnership::BucketOwnerPreferred); // Needed for the ACL tests.
    if (s_clientConfig->region != Aws::Region::US_EAST_1) {
        Aws::S3::Model::CreateBucketConfiguration createBucketConfiguration;
        createBucketConfiguration.WithLocationConstraint(
                Aws::S3::Model::BucketLocationConstraintMapper::GetBucketLocationConstraintForName(
                        s_clientConfig->region));
        request.WithCreateBucketConfiguration(createBucketConfiguration);
    }

    Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);
    bool result = true;
    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "S3_GTests::getCachedS3Bucket Error: CreateBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        result = false;
    }

    return result;
}


bool AwsDocTest::PAM_GTests::createTable(const Aws::String &tableName,
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
        result = waitTableActive(tableName);
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

bool AwsDocTest::PAM_GTests::deleteTable(const Aws::String &tableName) {
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


bool AwsDocTest::PAM_GTests::waitTableActive(const Aws::String &tableName) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(*s_clientConfig);
    // Repeatedly call DescribeTable until table is ACTIVE.
    const int MAX_QUERIES = 20;
    Aws::DynamoDB::Model::DescribeTableRequest request;
    request.SetTableName(tableName);

    int count = 0;
    while (count < MAX_QUERIES) {
        const Aws::DynamoDB::Model::DescribeTableOutcome &result = dynamoClient.DescribeTable(
                request);
        if (result.IsSuccess()) {
            Aws::DynamoDB::Model::TableStatus status = result.GetResult().GetTable().GetTableStatus();

            if (Aws::DynamoDB::Model::TableStatus::ACTIVE != status) {
                std::this_thread::sleep_for(std::chrono::seconds(1));
            }
            else {
                return true;
            }
        }
        else {
            std::cerr << "Error DynamoDB::waitTableActive "
                      << result.GetError().GetMessage() << std::endl;
            return false;
        }
        count++;
    }
    return false;
}


std::string AwsDocTest::PAM_GTests::getCachedBucketName() {
    if (s_cachedS3Bucket.empty()) {
        std::string cachedBucketName = uuidName("pam-test-");
        if (CreateBucket(cachedBucketName)) {
            s_cachedS3Bucket = cachedBucketName;
        }
    }

    return s_cachedS3Bucket;
}

std::string AwsDocTest::PAM_GTests::getCachedTableName() {
    if (s_cachedDynamoDBTable.empty()) {
        std::string cachedTableName = uuidName("PAM_TEST_");
        if (createTable(cachedTableName, "Label",
                        Aws::DynamoDB::Model::ScalarAttributeType::S)) {
            s_cachedDynamoDBTable = cachedTableName;
        }
    }

    return s_cachedDynamoDBTable;
}


int AwsDocTest::MyStringBuffer::underflow() {
    int result = basic_stringbuf::underflow();
    if (result == EOF) {
        std::cerr << "Error AwsDocTest::MyStringBuffer::underflow." << std::endl;
        throw std::underflow_error("AwsDocTest::MyStringBuffer::underflow");
    }

    return result;
}
