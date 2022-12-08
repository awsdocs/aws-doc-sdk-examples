/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

//snippet-start:[dynamodb.cpp.create_table_composite_key.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/KeySchemaElement.h>
#include <aws/dynamodb/model/ProvisionedThroughput.h>
#include <aws/dynamodb/model/ScalarAttributeType.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.create_table_composite_key.inc]
#include "dynamodb_samples.h"

// snippet-start:[dynamodb.cpp.create_table_composite_key.code]
//! Create an DynamoDB table with a composite key.
/*!
  \sa createDynamoDBTableWithCompositeKey()
  \param tableName: Name for the DynamoDB table.
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::DynamoDB::createDynamoDBTableWithCompositeKey(const Aws::String &tableName,
                                         const Aws::Client::ClientConfiguration &clientConfiguration)
{
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    std::cout << "Creating table " << tableName <<
              " with a composite primary key:\n" \
            "* Language - partition key\n" \
            "* Greeting - sort key\n";

    Aws::DynamoDB::Model::CreateTableRequest request;

    Aws::DynamoDB::Model::AttributeDefinition hashKey1, hashKey2;
    hashKey1.WithAttributeName("Language").WithAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
    request.AddAttributeDefinitions(hashKey1);
    hashKey2.WithAttributeName("Greeting").WithAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
    request.AddAttributeDefinitions(hashKey2);

    Aws::DynamoDB::Model::KeySchemaElement keySchemaElement1, keySchemaElement2;
    keySchemaElement1.WithAttributeName("Language").WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
    request.AddKeySchema(keySchemaElement1);
    keySchemaElement2.WithAttributeName("Greeting").WithKeyType(Aws::DynamoDB::Model::KeyType::RANGE);
    request.AddKeySchema(keySchemaElement2);

    Aws::DynamoDB::Model::ProvisionedThroughput throughput;
    throughput.WithReadCapacityUnits(5).WithWriteCapacityUnits(5);
    request.SetProvisionedThroughput(throughput);

    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::CreateTableOutcome& outcome = dynamoClient.CreateTable(request);
    if (outcome.IsSuccess())
    {
        std::cout << "Table \"" << outcome.GetResult().GetTableDescription().GetTableName() <<
                  "\" was created!" << std::endl;
    }
    else
    {
        std::cerr << "Failed to create table:" << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[dynamodb.cpp.create_table_composite_key.code]

/*
 *
 *  main function
 *
 *  Usage: 'run_create_table_composite_key <table>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc < 2)
    {
        std::cout << R"(
Usage:
    run_create_table_composite_key <table>
Where:
    table - the table to create)";
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String tableName(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::createDynamoDBTableWithCompositeKey(tableName, clientConfig);
     }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD