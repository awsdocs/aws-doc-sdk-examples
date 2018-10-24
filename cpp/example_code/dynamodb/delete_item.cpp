 
//snippet-sourcedescription:[delete_item.cpp demonstrates how to delete an item from an Amazon DynamoDB table.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/DeleteItemRequest.h>
#include <iostream>


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