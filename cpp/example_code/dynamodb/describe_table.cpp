//snippet-sourcedescription:[describe_table.cpp demonstrates how to retrieve information about an Amazon DynamoDB table.]
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

//snippet-start:[dynamodb.cpp.describe_table.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/DescribeTableRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.describe_table.inc]

/*
   Get information about (describe) a DynamoDB table.
   
   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
    const Aws::String USAGE = "\n" \
        "Usage:\n"
        "    describe_table <table>\n\n"
        "Where:\n"
        "    table - the table to describe.\n";

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

        // snippet-start:[dynamodb.cpp.describe_table.code]
        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        Aws::DynamoDB::Model::DescribeTableRequest dtr;
        dtr.SetTableName(table);

        const Aws::DynamoDB::Model::DescribeTableOutcome& result = dynamoClient.DescribeTable(dtr);

        if (result.IsSuccess())
        {
            const Aws::DynamoDB::Model::TableDescription& td = result.GetResult().GetTable();
            std::cout << "Table name  : " << td.GetTableName() << std::endl;
            std::cout << "Table ARN   : " << td.GetTableArn() << std::endl;
            std::cout << "Status      : " << Aws::DynamoDB::Model::TableStatusMapper::GetNameForTableStatus(td.GetTableStatus()) << std::endl;
            std::cout << "Item count  : " << td.GetItemCount() << std::endl;
            std::cout << "Size (bytes): " << td.GetTableSizeBytes() << std::endl;

            const Aws::DynamoDB::Model::ProvisionedThroughputDescription& ptd = td.GetProvisionedThroughput();
            std::cout << "Throughput" << std::endl;
            std::cout << "  Read Capacity : " << ptd.GetReadCapacityUnits() << std::endl;
            std::cout << "  Write Capacity: " << ptd.GetWriteCapacityUnits() << std::endl;

            const Aws::Vector<Aws::DynamoDB::Model::AttributeDefinition>& ad = td.GetAttributeDefinitions();
            std::cout << "Attributes" << std::endl;
            for (const auto& a : ad)
                std::cout << "  " << a.GetAttributeName() << " (" <<
                Aws::DynamoDB::Model::ScalarAttributeTypeMapper::GetNameForScalarAttributeType(a.GetAttributeType()) <<
                ")" << std::endl;
        }
        else
        {
            std::cout << "Failed to describe table: " << result.GetError().GetMessage();
        }
        // snippet-end:[dynamodb.cpp.describe_table.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}