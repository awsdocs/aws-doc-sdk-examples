 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/ProvisionedThroughput.h>
#include <aws/dynamodb/model/UpdateTableRequest.h>
#include <iostream>


/**
* Update a DynamoDB table.
*
* Takes the name of the table to update, the read capacity and the write
* capacity to use.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
    const std::string USAGE = \
        "Usage:\n"
        "    update_table <table> <read> <write>\n\n"
        "Where:\n"
        "    table - the table to put the item in.\n"
        "    read  - the new read capacity of the table.\n"
        "    write - the new write capacity of the table.\n\n"
        "Example:\n"
        "    update_table HelloTable 16 10\n";

    if (argc < 3)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const long long rc = Aws::Utils::StringUtils::ConvertToInt64(argv[2]);
        const long long wc = Aws::Utils::StringUtils::ConvertToInt64(argv[3]);

        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        std::cout << "Updating " << table << " with new provisioned throughput values" << std::endl;
        std::cout << "Read capacity : " << rc << std::endl;
        std::cout << "Write capacity: " << wc << std::endl;

        Aws::DynamoDB::Model::UpdateTableRequest utr;
        Aws::DynamoDB::Model::ProvisionedThroughput pt;
        pt.WithReadCapacityUnits(rc).WithWriteCapacityUnits(wc);
        utr.WithProvisionedThroughput(pt).WithTableName(table);

        const Aws::DynamoDB::Model::UpdateTableOutcome& result = dynamoClient.UpdateTable(utr);
        if (!result.IsSuccess())
        {
            std::cout << result.GetError().GetMessage() << std::endl;
            return 1;
        }
        std::cout << "Done!" << std::endl;
    }
    Aws::ShutdownAPI(options);
    return 0;
}