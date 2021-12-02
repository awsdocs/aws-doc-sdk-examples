//snippet-sourcedescription:[list_tables.cpp demonstrates how to list all Amazon DynamoDB tables for an AWS account.]
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

//snippet-start:[dynamodb.cpp.list_tables.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/ListTablesRequest.h>
#include <aws/dynamodb/model/ListTablesResult.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.list_tables.inc]

/*
   List DynamoDB tables for the current AWS account.

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/


int main(int argc, char** argv)
{
    std::cout << "Your DynamoDB Tables:" << std::endl;

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        // snippet-start:[dynamodb.cpp.list_tables.code]
        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        Aws::DynamoDB::Model::ListTablesRequest listTablesRequest;
        listTablesRequest.SetLimit(50);
        do
        {
            const Aws::DynamoDB::Model::ListTablesOutcome& lto = dynamoClient.ListTables(listTablesRequest);
            if (!lto.IsSuccess())
            {
                std::cout << "Error: " << lto.GetError().GetMessage() << std::endl;
                return 1;
            }
            
            for (const auto& s : lto.GetResult().GetTableNames())
                std::cout << s << std::endl;
            listTablesRequest.SetExclusiveStartTableName(lto.GetResult().GetLastEvaluatedTableName());
        
        } while (!listTablesRequest.GetExclusiveStartTableName().empty());
        // snippet-end:[dynamodb.cpp.list_tables.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}