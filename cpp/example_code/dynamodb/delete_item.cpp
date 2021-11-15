// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_item.cpp demonstrates how to delete an item from an Amazon DynamoDB table.]
*/
//snippet-start:[dynamodb.cpp.delete_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.delete_table.inc]

/**
* Deletes an item from a table.
*
* Takes name of table with string key "Name" and
* a value of an item to delete.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    DeleteItem <table> <name> <optional:region>\n\n"
        "Where:\n"
        "    table - the table to delete the item from.\n"
        "    name  - the item to delete from the table,\n"
        "            using the primary key \"Name\"\n"
        "    region- optional region\n\n"
        "Example:\n"
        "    DeleteItem HelloTable Name us-west-2\n\n"
        "**Warning** This program will actually delete the item\n"
        "            that you specify!\n";

    if (argc < 3)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const Aws::String name(argv[2]);
        const Aws::String region(argc > 3 ? argv[3] : "");

        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        std::cout << "Deleting item \"" << name <<
            "\" from table " << table << std::endl;

        Aws::DynamoDB::Model::DeleteItemRequest req;

        Aws::DynamoDB::Model::AttributeValue hashKey;
        hashKey.SetS(name);
        req.AddKey("Name", hashKey);
        req.SetTableName(table);

        const Aws::DynamoDB::Model::DeleteItemOutcome& result = dynamoClient.DeleteItem(req);
        if (result.IsSuccess())
        {
            std::cout << "Item \"" << name << "\" deleted!\n";
        }
        else
        {
            std::cout << "Failed to delete item: " << result.GetError().GetMessage();
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
