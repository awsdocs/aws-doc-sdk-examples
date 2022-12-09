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

//snippet-start:[dynamodb.cpp.list_tables.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/ListTablesRequest.h>
#include <aws/dynamodb/model/ListTablesResult.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.list_tables.inc]
#include "dynamodb_samples.h"

// snippet-start:[dynamodb.cpp.list_tables.code]
//! List the DynamoDB tables for the current AWS account.
/*!
  \sa listTables()
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
 */

bool listTables(const Aws::Client::ClientConfiguration &clientConfiguration)
{
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::ListTablesRequest listTablesRequest;
    listTablesRequest.SetLimit(50);
    do
    {
        const Aws::DynamoDB::Model::ListTablesOutcome& outcome = dynamoClient.ListTables(listTablesRequest);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error: " << outcome.GetError().GetMessage() << std::endl;
            return false;
        }

        for (const auto& tableName : outcome.GetResult().GetTableNames())
            std::cout << tableName << std::endl;
        listTablesRequest.SetExclusiveStartTableName(outcome.GetResult().GetLastEvaluatedTableName());

    } while (!listTablesRequest.GetExclusiveStartTableName().empty());

    return true;
}
// snippet-end:[dynamodb.cpp.list_tables.code]

/*
 *
 *  main function
 *
 *  Usage: 'run_list_tables'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    std::cout << "Your DynamoDB Tables:" << std::endl;

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::listTables(clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD