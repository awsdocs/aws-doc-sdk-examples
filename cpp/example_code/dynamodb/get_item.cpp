 
//snippet-sourcedescription:[get_item.cpp demonstrates how to retrieve an item from an Amazon DynamoDB table.]
//snippet-service:[dynamodb]
//snippet-keyword:[Amazon DynamoDB]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-09]
//snippet-sourceauthor:[AWS]

/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/

//snippet-start:[dynamodb.cpp.get_item.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/GetItemRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.get_item.inc]


/**
* Get an item from a DynamoDB table.
*
* Takes the name of the table and the name of the item to retrieve from it.
*
* The primary key "Name" is searched. By default, all fields and values 
* contained in the item are returned. If an optional projection expression is
* specified on the command line, only the specified fields and values are 
* returned.
*
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
        const Aws::String projection(argc > 3 ? argv[3] : "");

        // snippet-start:[dynamodb.cpp.get_item.code]
        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);
        Aws::DynamoDB::Model::GetItemRequest req;

        // Set up the request
        req.SetTableName(table);
        Aws::DynamoDB::Model::AttributeValue hashKey;
        hashKey.SetS(name);
        req.AddKey("Name", hashKey);
        if (!projection.empty())
            req.SetProjectionExpression(projection);

        // Retrieve the item's fields and values
        const Aws::DynamoDB::Model::GetItemOutcome& result = dynamoClient.GetItem(req);
        if (result.IsSuccess())
        {
            // Reference the retrieved fields/values
            const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>& item = result.GetResult().GetItem();
            if (item.size() > 0)
            {
                // Output each retrieved field and its value
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
        // snippet-end:[dynamodb.cpp.get_item.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}