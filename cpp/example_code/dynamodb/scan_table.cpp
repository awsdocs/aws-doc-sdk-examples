 
//snippet-sourcedescription:[get_item.cpp demonstrates how to perfrom scan operation on an Amazon DynamoDB table.]
//snippet-service:[dynamodb]
//snippet-keyword:[Amazon DynamoDB]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
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
//snippet-start:[dynamodb.cpp.scan_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/ScanRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.scan_table.inc]


/**
* Scan a DynamoDB table.
*
* Takes the name of the table.
*
* The table is scanned and all items are displayed. By default, all fields and values 
* contained in the item are returned. If an optional projection expression is
* specified on the command line, only the specified fields and values are 
* returned.
*
*/
int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    scan_table <table> [projection_expression]\n\n"
        "Where:\n"
        "    table - the table to Scan.\n\n"
        "You can add an optional projection expression (a quote-delimited,\n"
        "comma-separated list of attributes to retrieve) to limit the\n"
        "fields returned from the table.\n\n"
        "Example:\n"
        "    scan_table HelloTable \n"
        "    scan_table SiteColors \"default, bold\"\n";

    if (argc < 2)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const Aws::String projection(argc > 2 ? argv[2] : "");

        // snippet-start:[dynamodb.cpp.scan_table.code]
        Aws::Client::ClientConfiguration clientConfig;
       
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);
        Aws::DynamoDB::Model::ScanRequest req;
        req.SetTableName(table);

        if (!projection.empty())
            req.SetProjectionExpression(projection);        


        // Perform scan on table
        const Aws::DynamoDB::Model::ScanOutcome& result = dynamoClient.Scan(req);
        if (result.IsSuccess())
        {
            // Reference the retrieved items
            const Aws::Vector<Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>>& items = result.GetResult().GetItems();
            if(items.size() > 0) 
            {
                std::cout << "Number of items retrieved from scan: " << items.size() << std::endl;
                //Iterate each item and print
                for(const auto &item: items)
                {
                std::cout << "******************************************************" << std::endl;
                // Output each retrieved field and its value
                for (const auto& i : item)
                    std::cout << i.first << ": " << i.second.GetS() << std::endl;
                }
            }

            else
            {
                std::cout << "No item found in table: " << table << std::endl;
            }
        }
        else
        {
            std::cout << "Failed to Scan items: " << result.GetError().GetMessage();
        }
        // snippet-end:[dynamodb.cpp.scan_table.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}