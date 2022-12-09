
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
#include <aws/core/Aws.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/DeleteItemRequest.h>
#include <iostream>
#include "dynamodb_samples.h"

//snippet-start:[cpp.example_code.dynamodb.delete_item]
//! Deletes an item from a DynamoDB table.
/*!
  \sa deleteItem()
  \param tableName: The table name.
  \param partitionKey: The partition key.
  \param partitionValue: The value for the partition key.
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
 */

bool AwsDoc::DynamoDB::deleteItem(const Aws::String& tableName,
                const Aws::String& partitionKey,
                const Aws::String& partitionValue,
                const Aws::Client::ClientConfiguration &clientConfiguration)
{
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::DeleteItemRequest request;

    request.AddKey(partitionKey, Aws::DynamoDB::Model::AttributeValue().SetS(partitionValue));
    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::DeleteItemOutcome& outcome = dynamoClient.DeleteItem(request);
    if (outcome.IsSuccess())
    {
        std::cout << "Item \"" << partitionValue << "\" deleted!" << std::endl;
    }
    else
    {
        std::cerr << "Failed to delete item: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
//snippet-end:[cpp.example_code.dynamodb.delete_item]

/*
 *
 *  main function
 *
 *  Usage: 'run_delete_item <table_name> <partition_key> <partition_value>'
 *
 *  Prerequisites: a DynamoDB table named <table_name> containing an item with
 *     <partition_value> for its <partition_key>.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc < 4)
    {
        std::cout << R"("Usage:
    run_delete_item <table_name> <partition_key> <partition_value>
Where:
    table - the table to delete the item from.
    partition_key  - the partition key of the table,
    partition_value - the item value for the partition key"
Example:
    run_delete_item HelloTable Name Joe
**Warning** This program will actually delete the item
            that you specify!)";
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String tableName = (argv[1]);
        const Aws::String partitionKey = (argv[2]);
        const Aws::String partitionValue = (argv[3]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::deleteItem(tableName, partitionKey, partitionValue, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}
#endif // TESTING_BUILD