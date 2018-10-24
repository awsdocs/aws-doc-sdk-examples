 
//snippet-sourcedescription:[get_item.cpp demonstrates how to retrieve an item from an Amazon DynamoDB table.]
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
#include <aws/dynamodb/model/GetItemRequest.h>
#include <iostream>


/**
* Get an item from a DynamoDB table.
*
* Takes the name of the table and the name of the item to retrieve from it.
*
* The primary key searched is "Name", and the value contained by the field
* "Greeting" will be returned.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    get_item <table> <name> [projection_expression]\n\n"
        "Where:\n"
        "    table - the table to get an item from.\n"
        "    name  - the item to get.\n\n"
        "You can add an optional projection expression (a quote-delimited,\n"
        "comma-separated list of attributes to retrieve) to limit the\n"
        "fields returned from the table.\n\n"
        "Example:\n"
        "    get_item HelloTable World\n"
        "    get_item SiteColors text \"default, bold\"\n";

    if (argc < 2)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const Aws::String name(argv[2]);
        const Aws::String projection(argc > 3 ? argv[3] : "");

        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        Aws::DynamoDB::Model::GetItemRequest req;

        if (!projection.empty())
            req.SetProjectionExpression(projection);

        Aws::DynamoDB::Model::AttributeValue haskKey;
        haskKey.SetS(name);
        req.AddKey("Name", haskKey);

        req.SetTableName(table);

        const Aws::DynamoDB::Model::GetItemOutcome& result = dynamoClient.GetItem(req);
        if (result.IsSuccess())
        {
            const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>& item = result.GetResult().GetItem();
            if (item.size() > 0)
            {
                for (const auto& i : item)
                    std::cout << i.first << ": " << i.second.GetS() << std::endl;
            }
            else
            {
                std::cout << "No item found with the key " << name << std::endl;
            }

        }
        else
        {
            std::cout << "Failed to get item: " << result.GetError().GetMessage();
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}