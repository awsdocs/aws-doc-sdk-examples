//snippet-sourcedescription:[delete_table.cpp demonstrates how to delete an Amazon DynamoDB table.]
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


//snippet-start:[dynamodb.cpp.delete_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.delete_table.inc]

/*
   Delete a DynamoDB table.

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
    const Aws::String USAGE = "\n" \
        "Usage:\n"
        "     delete_table <table> \n\n"
        "Where:\n"
        "    table - the table to delete.\n"
        "Example:\n"
        "    delete_table HelloTable \n\n"
        "**Warning** This program will actually delete the table\n"
        "            that you specify!\n";

   if (argc < 1)
   {
        std::cout << USAGE;
        return 1;
   }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table = (argv[1]);
        const Aws::String region = "us-east-1"; 

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
            std::cout << "Your Table \"" << result.GetResult().GetTableDescription().GetTableName() << " was deleted!\n";
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