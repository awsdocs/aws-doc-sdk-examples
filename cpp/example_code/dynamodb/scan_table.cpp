//snippet-sourcedescription:[scan_table.cpp demonstrates how to scan an Amazon DynamoDB table]
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

//snippet-start:[dynamodb.cpp.scan_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/ScanRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.scan_table.inc]


/* 
   Scans a DynamoDB table.

  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
  For information, see this documentation topic:
  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
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
        const Aws::String table  = (argv[1]);
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