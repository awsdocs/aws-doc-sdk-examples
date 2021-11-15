// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_table.cpp demonstrates how to delete an Amazon DynamoDB table.
*/
//snippet-start:[dynamodb.cpp.delete_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.delete_table.inc]
/**
* Delete a DynamoDB table.
*
* Takes the name of the table to delete.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
    const Aws::String USAGE = "\n" \
        "Usage:\n"
        "     delete_table <table> <optional:region>\n\n"
        "Where:\n"
        "    table - the table to delete.\n"
        "    region- optional region\n\n"
        "Example:\n"
        "    delete_table HelloTable us-east-2\n\n"
        "**Warning** This program will actually delete the table\n"
        "            that you specify!\n";

    if (argc < 2)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const Aws::String region(argc > 2 ? argv[2] : "");

        // snippet-start:[dynamodb.cpp.delete_table.code]
        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        Aws::DynamoDB::Model::DeleteTableRequest dtr;
        dtr.SetTableName(table);

        const Aws::DynamoDB::Model::DeleteTableOutcome& result = dynamoClient.DeleteTable(dtr);
        if (result.IsSuccess())
        {
            std::cout << "Table \"" << result.GetResult().GetTableDescription().GetTableName() <<
                " deleted!\n";
        }
        else
        {
            std::cout << "Failed to delete table: " << result.GetError().GetMessage();
        }
        // snippet-end:[dynamodb.cpp.delete_table.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
