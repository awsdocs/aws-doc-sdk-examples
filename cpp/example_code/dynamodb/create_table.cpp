//snippet-sourcedescription:[create_table.cpp demonstrates how to create an Amazon DynamoDB table.]
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

//snippet-start:[dynamodb.cpp.create_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/KeySchemaElement.h>
#include <aws/dynamodb/model/ProvisionedThroughput.h>
#include <aws/dynamodb/model/ScalarAttributeType.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.create_table.inc]

/*
   Create an Amazon DynamoDB table.

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    create_table <table> <optional:region>\n\n"
        "Where:\n"
        "    table - the table to create\n"
        "Example:\n"
        "    create_table HelloTable \n";

    if (argc < 1)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table = (argv[1]);
        const Aws::String region = "us-east-1"  ; 

        // snippet-start:[dynamodb.cpp.create_table.code]
        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        std::cout << "Creating table " << table <<
            " with a simple primary key: \"Name\"" << std::endl;

        Aws::DynamoDB::Model::CreateTableRequest req;

        Aws::DynamoDB::Model::AttributeDefinition haskKey;
        haskKey.SetAttributeName("Name");
        haskKey.SetAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
        req.AddAttributeDefinitions(haskKey);

        Aws::DynamoDB::Model::KeySchemaElement keyscelt;
        keyscelt.WithAttributeName("Name").WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
        req.AddKeySchema(keyscelt);

        Aws::DynamoDB::Model::ProvisionedThroughput thruput;
        thruput.WithReadCapacityUnits(5).WithWriteCapacityUnits(5);
        req.SetProvisionedThroughput(thruput);
        req.SetTableName(table);

        const Aws::DynamoDB::Model::CreateTableOutcome& result = dynamoClient.CreateTable(req);
        if (result.IsSuccess())
        {
            std::cout << "Table \"" << result.GetResult().GetTableDescription().GetTableName() <<
                " created!" << std::endl;
        }
        else
        {
            std::cout << "Failed to create table: " << result.GetError().GetMessage();
        }
        // snippet-end:[dynamodb.cpp.create_table.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}