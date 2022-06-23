//snippet-sourcedescription:[create_table_composite_key.cpp demonstrates how to create an Amazon DynamoDB table that has a composite key.]
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

//snippet-start:[dynamodb.cpp.create_table_composite_key.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/KeySchemaElement.h>
#include <aws/dynamodb/model/ProvisionedThroughput.h>
#include <aws/dynamodb/model/ScalarAttributeType.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.create_table_composite_key.inc]

/**
   Create a DynamoDB table.

   Takes the name of the table to create. The table will contain a
   composite key, "Language" (hash) and "Greeting" (range).

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    create_table_composite_key <table> <optional:region>\n\n"
        "Where:\n"
        "    table - the table to create\n"
        "    region- optional region\n\n"
        "Example:\n"
        "    create_table_composite_key HelloTable us-west-2\n";

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

        // snippet-start:[dynamodb.cpp.create_table_composite_key.code]
        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        std::cout << "Creating table " << table <<
            " with a composite primary key:\n" \
            "* Language - partition key\n" \
            "* Greeting - sort key\n";

        Aws::DynamoDB::Model::CreateTableRequest req;

        Aws::DynamoDB::Model::AttributeDefinition hashKey1, hashKey2;
        hashKey1.WithAttributeName("Language").WithAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
        req.AddAttributeDefinitions(hashKey1);
        hashKey2.WithAttributeName("Greeting").WithAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
        req.AddAttributeDefinitions(hashKey2);

        Aws::DynamoDB::Model::KeySchemaElement kse1, kse2;
        kse1.WithAttributeName("Language").WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
        req.AddKeySchema(kse1);
        kse2.WithAttributeName("Greeting").WithKeyType(Aws::DynamoDB::Model::KeyType::RANGE);
        req.AddKeySchema(kse2);

        Aws::DynamoDB::Model::ProvisionedThroughput thruput;
        thruput.WithReadCapacityUnits(5).WithWriteCapacityUnits(5);
        req.SetProvisionedThroughput(thruput);

        req.SetTableName(table);

        const Aws::DynamoDB::Model::CreateTableOutcome& result = dynamoClient.CreateTable(req);
        if (result.IsSuccess())
        {
            std::cout << "Table \"" << result.GetResult().GetTableDescription().GetTableName() <<
                "\" was created!\n";
        }
        else
        {
            std::cout << "Failed to create table:" << result.GetError().GetMessage();
        }
        // snippet-end:[dynamodb.cpp.create_table_composite_key.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}