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
#include <aws/dynamodb/model/ScanRequest.h>
#include <iostream>
#include "dynamodb_samples.h"

// snippet-start:[dynamodb.cpp.scan_table.code]
//! Scans a DynamoDB table.
/*!
  \sa scanTable()
  \param tableName: Name for the DynamoDB table.
  \param projectionExpression: An optional projection expression, ignored if empty.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */

bool AwsDoc::DynamoDB::scanTable(const Aws::String &tableName,
                                 const Aws::String &projectionExpression,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);
    Aws::DynamoDB::Model::ScanRequest request;
    request.SetTableName(tableName);

    if (!projectionExpression.empty())
        request.SetProjectionExpression(projectionExpression);

    // Perform scan on table
    const Aws::DynamoDB::Model::ScanOutcome &outcome = dynamoClient.Scan(request);
    if (outcome.IsSuccess()) {
        // Reference the retrieved items
        const Aws::Vector<Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>> &items = outcome.GetResult().GetItems();
        if (!items.empty()) {
            std::cout << "Number of items retrieved from scan: " << items.size()
                      << std::endl;
            //Iterate each item and print
            for (const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &itemMap: items) {
                std::cout << "******************************************************"
                          << std::endl;
                // Output each retrieved field and its value
                for (const auto &itemEntry: itemMap)
                    std::cout << itemEntry.first << ": " << itemEntry.second.GetS()
                              << std::endl;
            }
        }

        else {
            std::cout << "No item found in table: " << tableName << std::endl;
        }
    }
    else {
        std::cerr << "Failed to Scan items: " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

// snippet-start:[dynamodb.cpp.scan_table.code]

/*
 *  main function
 *
 *  Usage: 'run_scan_table <table> [projection_expression]'
 *
 *  Prerequisites: A pre-populated DynamoDB table.
 *
 *  Instructions for populating a table with sample data can be found at:
 *  https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SampleData.html
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 2) {
        std::cout << R"(
Usage:
    run_scan_table <table> [projection_expression]
Where:
    table - the table to Scan.
You can add an optional projection expression (a quote-delimited,
comma-separated list of attributes to retrieve) to limit the
fields returned from the table.
)";
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String tableName = (argv[1]);
        const Aws::String projectionExpression(argc > 2 ? argv[2] : "");

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::scanTable(tableName, projectionExpression, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD