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
#include <aws/dynamodb/model/QueryRequest.h>
#include <iostream>
#include "dynamodb_samples.h"

// snippet-start:[dynamodb.cpp.query_items.code]
//! Perform  a query on a DynamoDB Table and retrieve items.
/*!
  \sa queryItem()
  \param tableName: The table name.
  \param partitionKey: The partition key.
  \param partitionValue: The value for the partition key.
  \param projectionExpression: The projections expression, which is ignored if empty.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
  */

/*
 * The partition key attribute is searched with the specified value. By default, all fields and values
 * contained in the item are returned. If an optional projection expression is
 * specified on the command line, only the specified fields and values are
 * returned.
 */

bool AwsDoc::DynamoDB::queryItems(const Aws::String &tableName,
                                  const Aws::String &partitionKey,
                                  const Aws::String &partitionValue,
                                  const Aws::String &projectionExpression,
                                  const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);
    Aws::DynamoDB::Model::QueryRequest request;

    request.SetTableName(tableName);

    if (!projectionExpression.empty()) {
        request.SetProjectionExpression(projectionExpression);
    }

    // Set query key condition expression
    request.SetKeyConditionExpression(partitionKey + "= :valueToMatch");

    // Set Expression AttributeValues
    Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> attributeValues;
    attributeValues.emplace(":valueToMatch", partitionValue);

    request.SetExpressionAttributeValues(attributeValues);

    bool result = true;

    // "exclusiveStartKey" is used for pagination.
    Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> exclusiveStartKey;
    do {
        if (!exclusiveStartKey.empty()) {
            request.SetExclusiveStartKey(exclusiveStartKey);
            exclusiveStartKey.clear();
        }
        // Perform Query operation
        const Aws::DynamoDB::Model::QueryOutcome &outcome = dynamoClient.Query(request);
        if (outcome.IsSuccess()) {
            // Reference the retrieved items
            const Aws::Vector<Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>> &items = outcome.GetResult().GetItems();
            if (!items.empty()) {
                std::cout << "Number of items retrieved from Query: " << items.size()
                          << std::endl;
                //Iterate each item and print
                for (const auto &item: items) {
                    std::cout
                            << "******************************************************"
                            << std::endl;
                    // Output each retrieved field and its value
                    for (const auto &i: item)
                        std::cout << i.first << ": " << i.second.GetS() << std::endl;
                }
            }
            else {
                std::cout << "No item found in table: " << tableName << std::endl;
            }

            exclusiveStartKey = outcome.GetResult().GetLastEvaluatedKey();
        }
        else {
            std::cerr << "Failed to Query items: " << outcome.GetError().GetMessage();
            result = false;
            break;
        }
    } while (!exclusiveStartKey.empty());

    return result;
}
// snippet-end:[dynamodb.cpp.query_items.code]

/*
 *  main function
 *
 *  Usage: 'run_query_items <table_name> <partition_key> <partition_value> [projection_expression]'
 *
 *  Prerequisites: A pre-populated DynamoDB table.
 *
 *  Instructions for populating a table with sample data can be found at:
 *  https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SampleData.html
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 4) {
        std::cout << R"(
Usage:
    run_query_items <table_name> <partition_key> <partition_value> [projection_expression]
Where:
    table_name - the table to get an item from
    partition_key  - the partition key attribute of the table
    partition_value  - the partition key value to query
    [projection_expression] - the projection expression
)";
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String tableName = (argv[1]);
        const Aws::String partitionKey = (argv[2]);
        const Aws::String partitionValue = (argv[3]);

        const Aws::String projection(argc > 4 ? argv[4] : "");

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::queryItem(tableName, partitionKey, partitionValue, projection,
                                    clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif //  TESTING_BUILD