// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
create_table.cpp demonstrates how to create an Amazon DynamoDB table.]
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


/**
* Create an Amazon DynamoDB table.
*/
int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    create_table <table> <optional:region>\n\n"
        "Where:\n"
        "    table - the table to create\n"
        "    region- optional region\n\n"
        "Example:\n"
        "    create_table HelloTable us-west-2\n";

    if (argc < 2)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const Aws::String region(argc > 2 ? argv[2] : "");

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
