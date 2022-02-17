//snippet-sourcedescription:[query_items.cpp demonstrates how to perfrom Query operation and retrieve items from an Amazon DynamoDB table.]
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

//snippet-start:[dynamodb.cpp.query_items.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/QueryRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.query_items.inc]

/**
  Perform query on a DynamoDB Table and retrieve item(s).

  The partition key attribute is searched with the specified value. By default, all fields and values 
  contained in the item are returned. If an optional projection expression is
  specified on the command line, only the specified fields and values are 
  returned.
 
  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
  For information, see this documentation topic:
  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    query_items <table> <partitionKeyAttributeName>=<partitionKeyValue> [projection_expression]\n\n"
        "Where:\n"
        "    table - the table to get an item from.\n"
        "    partitionKeyAttributeName  - Partition Key attribute of the table.\n"
        "    partitionKeyValue  - Partition Key value to query.\n\n"
        "Example:\n"
        "    query_items HelloTable Name=Namaste\n"
        "    query_items Players FirstName=Mike\n"
        "    query_items SiteColors Background=white \"default, bold\"\n";

    if (argc < 2)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table = (argv[1]);
        const Aws::String partitionKeyNameAndValue = (argv[2]);
        Aws::String partitionKeyAttributeName("");
        Aws::String partitionKeyAttributeValue("");

        // Split and get partitionKeyAttributeName and partitionKeyAttributeValue
        const Aws::Vector<Aws::String>& flds = Aws::Utils::StringUtils::Split(partitionKeyNameAndValue, '=');
            if (flds.size() == 2)
            {
                partitionKeyAttributeName = flds[0];
                partitionKeyAttributeValue = flds[1];
            }
            else
            {
                std::cout << "Invalid argument: " << partitionKeyNameAndValue << std::endl << USAGE;
                return 1;
            }

        const Aws::String projection(argc > 3 ? argv[3] : "");

        // snippet-start:[dynamodb.cpp.query_items.code]
        Aws::Client::ClientConfiguration clientConfig;
       
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);
        Aws::DynamoDB::Model::QueryRequest req;
        
        req.SetTableName(table);

        // Set query key condition expression
        req.SetKeyConditionExpression(partitionKeyAttributeName + "= :valueToMatch");

        // Set Expression AttributeValues
        Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> attributeValues;
        attributeValues.emplace(":valueToMatch", partitionKeyAttributeValue);

        req.SetExpressionAttributeValues(attributeValues);

        // Perform Query operation
        const Aws::DynamoDB::Model::QueryOutcome& result = dynamoClient.Query(req);
        if (result.IsSuccess())
        {
            // Reference the retrieved items
            const Aws::Vector<Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>>& items = result.GetResult().GetItems();
            if(items.size() > 0) 
            {
                std::cout << "Number of items retrieved from Query: " << items.size() << std::endl;
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
            std::cout << "Failed to Query items: " << result.GetError().GetMessage();
        }
        // snippet-end:[dynamodb.cpp.query_items.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}