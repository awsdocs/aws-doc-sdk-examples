//snippet-sourcedescription:[delete_item.cpp demonstrates how to delete an item from an Amazon DynamoDB table.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/30/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
//snippet-start:[dynamodb.cpp.delete_item.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/DeleteItemRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.delete_item.inc]


/*
   Deletes an item from a table.

   Takes name of table with string key "id" and
   a value of an item to delete.

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/
int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    DeleteItem <table> <name> \n\n"
        "Where:\n"
        "    table - the table to delete the item from.\n"
        "    name  - the item to delete from the table,\n"
        "            using the primary key \"Name\"\n"
        "Example:\n"
        "    DeleteItem HelloTable Name us-west-2\n\n"
        "**Warning** This program will actually delete the item\n"
        "            that you specify!\n";

    if (argc < 2)
    {
        std::cout << USAGE;
        return 1;
    }

    //snippet-start:[dynamodb.cpp.delete_item.code]
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table = (argv[1]);
        const Aws::String name = (argv[2]);
        const Aws::String region = "us-east-1";

        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        std::cout << "Deleting item \"" << name <<
            "\" from table " << table << std::endl;

        Aws::DynamoDB::Model::DeleteItemRequest req;

        Aws::DynamoDB::Model::AttributeValue hashKey;
        hashKey.SetS(name);
        req.AddKey("id", hashKey);
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
    //snippet-end:[dynamodb.cpp.delete_item.code]
}