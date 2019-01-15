 
//snippet-sourcedescription:[delete_table.cpp demonstrates how to delete an Amazon DynamoDB table.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-service:[dynamodb]
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
        const Aws::String region(argc > 1 ? argv[2] : "");

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