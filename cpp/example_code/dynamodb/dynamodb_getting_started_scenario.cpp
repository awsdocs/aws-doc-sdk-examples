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
#include <aws/dynamodb/model/PutItemRequest.h>
#include <aws/dynamodb/model/UpdateItemRequest.h>

namespace AwsDoc {
    namespace DynamoDB {
        static const Aws::String MOVIE_TABLE_NAME("doc-example-table-movies");
        static const Aws::String YEAR_KEY("year");
        static const Aws::String TITLE_KEY("title");
        static const Aws::String RATING_KEY("rating");
        static const Aws::String PLOT_KEY("plot");
        static const int PROVISIONED_THROUGHPUT_UNITS = 10;

        static bool deleteDynamoTable(const Aws::String &tableName,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);

        static Aws::String askQuestion(const Aws::String &string,
                                       std::function<bool(Aws::String)> test = [](
                                               const Aws::String &) -> bool { return true; });

        int askQuestionForInt(const Aws::String &string);

        float askQuestionForFloatRange(const Aws::String &string, float low, float high);

        static bool waitTableActive(const Aws::String &tableName,
                                    const Aws::Client::ClientConfiguration &clientConfiguration);

    } //  namespace DynamoDB
} // namespace AwsDoc

/*
 *
 * 1. Create a table with partition: year (N) and sort: title (S) (CreateTable)
 * 2. Add a new movie. (PutItem)
 * 3. Update the rating and plot of the movie by using an update expression. (UpdateItem with UpdateExpression + ExpressionAttributeValues args)
 * 4. Put movies in the table from moviedata.json--download it from the DynamoDB guide. OK to include manual download and unzip steps in your example README. This is a large file so limit the number of movies to 250 or so. (BatchWriteItem)
 * 5. Get a movie by Key (partition + sort) (GetItem)
 * 6. Delete a movie. (DeleteItem)
 * 7. Use Query with a key condition expression to return all movies released in a given year. (Query + KeyConditionExpression arg)
 * 8. Use Scan to return movies released within a range of years. Show how to paginate data using ExclusiveStartKey. (Scan + FilterExpression)
 * 9. Delete the table. (DeleteTable)
 */

bool AwsDoc::DynamoDB::dynamodbGettingStartedScenario(const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::cout << std::setfill('*') << std::setw(88) << " " << std::endl;
    std::cout << "Welcome to the Amazon DynamoDB getting started demo." << std::endl;
    std::cout << std::setfill('*') << std::setw(88) << " " << std::endl;

    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    // 1. Create a table with partition: year (N) and sort: title (S) (CreateTable)
    {
        Aws::DynamoDB::Model::CreateTableRequest request;

        Aws::DynamoDB::Model::AttributeDefinition yearAttributeDefinition;
        yearAttributeDefinition.SetAttributeName(YEAR_KEY);
        yearAttributeDefinition.SetAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::N);
        request.AddAttributeDefinitions(yearAttributeDefinition);

        Aws::DynamoDB::Model::AttributeDefinition titleAttributeDefinition;
        yearAttributeDefinition.SetAttributeName(TITLE_KEY);
        yearAttributeDefinition.SetAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
        request.AddAttributeDefinitions(yearAttributeDefinition);

        Aws::DynamoDB::Model::KeySchemaElement yearKeySchema;
        yearKeySchema.WithAttributeName(YEAR_KEY).WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
        request.AddKeySchema(yearKeySchema);

        Aws::DynamoDB::Model::KeySchemaElement titleKeySchema;
        yearKeySchema.WithAttributeName(TITLE_KEY).WithKeyType(Aws::DynamoDB::Model::KeyType::RANGE);
        request.AddKeySchema(yearKeySchema);

        Aws::DynamoDB::Model::ProvisionedThroughput throughput;
        throughput.WithReadCapacityUnits(PROVISIONED_THROUGHPUT_UNITS).WithWriteCapacityUnits(
                PROVISIONED_THROUGHPUT_UNITS);
        request.SetProvisionedThroughput(throughput);
        request.SetTableName(MOVIE_TABLE_NAME);

        std::cout << "Creating table '" << MOVIE_TABLE_NAME << "'..." << std::endl;
        const Aws::DynamoDB::Model::CreateTableOutcome &result = dynamoClient.CreateTable(request);
        if (!result.IsSuccess()) {
            if (result.GetError().GetErrorType() == Aws::DynamoDB::DynamoDBErrors::RESOURCE_IN_USE) {
                std::cout << "Table already exists." << std::endl;
            }
            else {
                std::cerr << "Failed to create table: " << result.GetError().GetMessage();
                return false;
            }
        }
    }

    std::cout << "Waiting for table '" << MOVIE_TABLE_NAME << "' to become active...." << std::endl;
    if (!AwsDoc::DynamoDB::waitTableActive(MOVIE_TABLE_NAME, clientConfiguration)) {
        deleteDynamoTable(MOVIE_TABLE_NAME, clientConfiguration);
    }
    std::cout << "Table '" << MOVIE_TABLE_NAME << "' created and active." << std::endl;

    // 2. Add a new movie. (PutItem)
    Aws::String title;
    int year;
    {
        title = askQuestion("Enter the title of a movie you want to add to the table: ");
        year = askQuestionForInt("What year was it released? ");
        float rating = askQuestionForFloatRange("On a scale of 1 - 10, how do you rate it? ", 1, 10);
        Aws::String plot = askQuestion("Summarize the plot for me: ");

        Aws::DynamoDB::Model::PutItemRequest putItemRequest;
        putItemRequest.SetTableName(MOVIE_TABLE_NAME);

        putItemRequest.AddItem(YEAR_KEY, Aws::DynamoDB::Model::AttributeValue().SetN(year));
        putItemRequest.AddItem(TITLE_KEY, Aws::DynamoDB::Model::AttributeValue().SetS(title));
        putItemRequest.AddItem(RATING_KEY, Aws::DynamoDB::Model::AttributeValue().SetN(rating));
        putItemRequest.AddItem(PLOT_KEY, Aws::DynamoDB::Model::AttributeValue().SetS(plot));

        Aws::DynamoDB::Model::PutItemOutcome outcome = dynamoClient.PutItem(putItemRequest);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to add an item: " << outcome.GetError().GetMessage();
            deleteDynamoTable(MOVIE_TABLE_NAME, clientConfiguration);
            return false;
        }
    }

    std::cout << "\nAdded '" << title << "' to '" << MOVIE_TABLE_NAME << "'." << std::endl;
    std::this_thread::sleep_for(std::chrono::seconds(10));
    // 3. Update the rating and plot of the movie by using an update expression.
    // (UpdateItem with UpdateExpression + ExpressionAttributeValues args)
    {
        float rating = askQuestionForFloatRange("On a scale of 1 - 10, how do you rate it? ", 1, 10);
        Aws::String plot = askQuestion("Summarize the plot for me: ");

        Aws::DynamoDB::Model::UpdateItemRequest request;
        request.SetTableName(MOVIE_TABLE_NAME);
        request.AddKey(TITLE_KEY, Aws::DynamoDB::Model::AttributeValue().SetS(title));
        request.AddKey(YEAR_KEY, Aws::DynamoDB::Model::AttributeValue().SetN(year));
        request.SetUpdateExpression("set info.rating=:r, info.plot=:p");
        request.SetExpressionAttributeValues({
                                                     {":r", Aws::DynamoDB::Model::AttributeValue().SetN(rating)},
                                                     {":p", Aws::DynamoDB::Model::AttributeValue().SetS(plot)}
                                             });

        const Aws::DynamoDB::Model::UpdateItemOutcome& result = dynamoClient.UpdateItem(request);
        if (!result.IsSuccess())
        {
            std::cerr << "Error updating movie " + result.GetError().GetMessage() << std::endl;
            deleteDynamoTable(MOVIE_TABLE_NAME, clientConfiguration);
            return false;
        }
    }

    std::cout << "\nUpdated '" << title << "' with new attributes:" << std::endl;

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


Aws::String AwsDoc::DynamoDB::askQuestion(const Aws::String &string,
                                          std::function<bool(Aws::String)> test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
        if (!test(result)) {
            continue;
        }
    } while (result.empty());

    return result;
}

int AwsDoc::DynamoDB::askQuestionForInt(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](const Aws::String &string1) -> bool {
        try {
            std::atoi(string1.c_str());
            return true;
        }
        catch (std::invalid_argument) {
            return false;
        }
    });

    int result = 0;
    try {
        result = std::atoi(resultString.c_str());
    }
    catch (std::invalid_argument) {
        std::cerr << "DynamoDB::askQuestionForInt string not an int "
                  << resultString << std::endl;
    }
    return result;
}

float AwsDoc::DynamoDB::askQuestionForFloatRange(const Aws::String &string, float low, float high) {
    Aws::String resultString = askQuestion(string, [low, high](const Aws::String &string1) -> bool {
        try {
            float number = std::atof(string1.c_str());
            return number >= low && number <= high;
        }
        catch (std::invalid_argument) {
            return false;
        }
    });
    float result = 0;
    try {
        result = std::atof(resultString.c_str());
    }
    catch (std::invalid_argument) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
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