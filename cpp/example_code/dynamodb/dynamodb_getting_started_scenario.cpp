/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "dyanamodb_samples.h"
#include <iostream>
#include <iomanip>
#include <aws/core/Aws.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <aws/dynamodb/model/DescribeTableRequest.h>

namespace AwsDoc {
    namespace DynamoDB {
        static const Aws::String MOVIE_TABLE_NAME("doc-example-table-movies");
        static const int PROVISIONED_THROUGHPUT_UNITS = 10;

        static bool deleteDynamoTable(const Aws::String &tableName,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);

        static Aws::String askQuestion(const Aws::String &string);

        static bool waitTableActive(const Aws::String &tableName,
                                         const Aws::Client::ClientConfiguration &clientConfiguration);
    } //  namespace DynamoDB
} // namespace AwsDoc

bool AwsDoc::DynamoDB::dynamodbGettingStartedScenario(const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::cout << std::setfill('*') << std::setw(88) << " " << std::endl;
    std::cout << "Welcome to the Amazon DynamoDB getting started demo." << std::endl;
    std::cout << std::setfill('*') << std::setw(88) << " " << std::endl;

    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::CreateTableRequest request;

    Aws::DynamoDB::Model::AttributeDefinition yearAttributeDefinition;
    yearAttributeDefinition.SetAttributeName("year");
    yearAttributeDefinition.SetAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
    request.AddAttributeDefinitions(yearAttributeDefinition);

    Aws::DynamoDB::Model::AttributeDefinition titleAttributeDefinition;
    yearAttributeDefinition.SetAttributeName("title");
    yearAttributeDefinition.SetAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
    request.AddAttributeDefinitions(yearAttributeDefinition);

    Aws::DynamoDB::Model::KeySchemaElement yearKeySchema;
    yearKeySchema.WithAttributeName("year").WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
    request.AddKeySchema(yearKeySchema);

    Aws::DynamoDB::Model::KeySchemaElement titleKeySchema;
    yearKeySchema.WithAttributeName("title").WithKeyType(Aws::DynamoDB::Model::KeyType::RANGE);
    request.AddKeySchema(yearKeySchema);

    Aws::DynamoDB::Model::ProvisionedThroughput throughput;
    throughput.WithReadCapacityUnits(PROVISIONED_THROUGHPUT_UNITS).WithWriteCapacityUnits(PROVISIONED_THROUGHPUT_UNITS);
    request.SetProvisionedThroughput(throughput);
    request.SetTableName(MOVIE_TABLE_NAME);

    std::cout << "Creating table '" << MOVIE_TABLE_NAME << "'..." << std::endl;
    const Aws::DynamoDB::Model::CreateTableOutcome &result = dynamoClient.CreateTable(request);
    if (!result.IsSuccess()) {
        if (result.GetError().GetErrorType() == Aws::DynamoDB::DynamoDBErrors::RESOURCE_IN_USE)
        {
            std::cout << "Table already exists." << std::endl;
        }
        else
        {
            std::cerr << "Failed to create table: " << result.GetError().GetMessage();
            return false;
        }
    }
    std::cout << "Waiting for table '" << MOVIE_TABLE_NAME << "' to become active...." << std::endl;
    if (!AwsDoc::DynamoDB::waitTableActive(MOVIE_TABLE_NAME, clientConfiguration))
    {
        deleteDynamoTable(MOVIE_TABLE_NAME, clientConfiguration);
    }

    std::cout << "Table '" << MOVIE_TABLE_NAME << "' created and active." << std::endl;

    Aws::String title = askQuestion("Enter the title of a movie you want to add to the table: ");
    if (title.empty()) {
        deleteDynamoTable(MOVIE_TABLE_NAME, clientConfiguration);
        return false;
    }

    return deleteDynamoTable(MOVIE_TABLE_NAME, clientConfiguration);
}

bool AwsDoc::DynamoDB::deleteDynamoTable(const Aws::String &tableName,
                                         const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::DeleteTableRequest request;
    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::DeleteTableOutcome &result = dynamoClient.DeleteTable(request);
    if (result.IsSuccess()) {
        std::cout << "Your Table \"" << result.GetResult().GetTableDescription().GetTableName() << " was deleted!\n";
    }
    else {
        std::cerr << "Failed to delete table: " << result.GetError().GetMessage();
    }

    return result.IsSuccess();
}

Aws::String AwsDoc::DynamoDB::askQuestion(const Aws::String &string) {
    Aws::String result;
    do {
        std::cout << string << std::endl;
        std::cin >> result;
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
    } while (result.empty());

    return result;
}

bool AwsDoc::DynamoDB::waitTableActive(const Aws::String &tableName,
                                            const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient client(clientConfiguration);
    // Repeatedly call DescribeTable until table is ACTIVE
    Aws::DynamoDB::Model::DescribeTableRequest request;
    request.SetTableName(tableName);

    int count = 0;
    while (count < 20) {
        const Aws::DynamoDB::Model::DescribeTableOutcome &result = client.DescribeTable(request);
        if (!result.IsSuccess()) {
            std::cerr << "Error DynamoDB::waitTableActiveconst "
                      << result.GetError().GetMessage() << std::endl;
            return false;
        }
        else {
            Aws::DynamoDB::Model::TableStatus status = result.GetResult().GetTable().GetTableStatus();

            if (Aws::DynamoDB::Model::TableStatus::ACTIVE == status) {
                return true;
            }
            else {
                std::this_thread::sleep_for(std::chrono::seconds(1));
            }
        }
        count++;
    }
    return false;
}

#ifndef TESTING_BUILD
    int main(int argc, char **argv) {
        Aws::SDKOptions options;
        InitAPI(options);

        {
            Aws::Client::ClientConfiguration clientConfig;
            AwsDoc::DynamoDB::dynamodbGettingStartedScenario(clientConfig);
        }
        return 0;
    }

#endif // TESTING_BUILD