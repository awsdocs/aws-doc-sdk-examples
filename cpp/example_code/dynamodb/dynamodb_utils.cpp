/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 *
 * Purpose
 *
 * Utility routines used by multiple applications.
 *
 */

#include "dyanamodb_samples.h"
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <aws/dynamodb/model/DescribeTableRequest.h>

namespace AwsDoc {
    namespace DynamoDB {
        /**
         * Constants for DynamoDB table creation and access.
         */
        const Aws::String MOVIE_TABLE_NAME("doc-example-table-movies");
        const Aws::String YEAR_KEY("year");
        const Aws::String TITLE_KEY("title");
        const Aws::String INFO_KEY("info");
        const Aws::String RATING_KEY("rating");
        const Aws::String PLOT_KEY("plot");
        const int PROVISIONED_THROUGHPUT_UNITS = 10;
        const Aws::String ALLOCATION_TAG("dynamodb_scenario");
        const int ASTERIX_FILL_WIDTH = 88;
    } //  namespace DynamoDB
} // namespace AwsDoc


// snippet-start:[cpp.example_code.dynamodb.scenario.createTable]
//! Create a DynamoDB table.
/*!
  \sa createDynamoDBTable()
  \param tableName: The DynamoDB table's name.
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
*/
bool AwsDoc::DynamoDB::createDynamoDBTable(const Aws::String &tableName,
                                           const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    bool movieTableAlreadyExisted = false;

    // 1. Create a table.
    {
        Aws::DynamoDB::Model::CreateTableRequest request;

        Aws::DynamoDB::Model::AttributeDefinition yearAttributeDefinition;
        yearAttributeDefinition.SetAttributeName(YEAR_KEY);
        yearAttributeDefinition.SetAttributeType(
                Aws::DynamoDB::Model::ScalarAttributeType::N);
        request.AddAttributeDefinitions(yearAttributeDefinition);

        Aws::DynamoDB::Model::AttributeDefinition titleAttributeDefinition;
        yearAttributeDefinition.SetAttributeName(TITLE_KEY);
        yearAttributeDefinition.SetAttributeType(
                Aws::DynamoDB::Model::ScalarAttributeType::S);
        request.AddAttributeDefinitions(yearAttributeDefinition);

        Aws::DynamoDB::Model::KeySchemaElement yearKeySchema;
        yearKeySchema.WithAttributeName(YEAR_KEY).WithKeyType(
                Aws::DynamoDB::Model::KeyType::HASH);
        request.AddKeySchema(yearKeySchema);

        Aws::DynamoDB::Model::KeySchemaElement titleKeySchema;
        yearKeySchema.WithAttributeName(TITLE_KEY).WithKeyType(
                Aws::DynamoDB::Model::KeyType::RANGE);
        request.AddKeySchema(yearKeySchema);

        Aws::DynamoDB::Model::ProvisionedThroughput throughput;
        throughput.WithReadCapacityUnits(
                PROVISIONED_THROUGHPUT_UNITS).WithWriteCapacityUnits(
                PROVISIONED_THROUGHPUT_UNITS);
        request.SetProvisionedThroughput(throughput);
        request.SetTableName(MOVIE_TABLE_NAME);

        std::cout << "Creating table '" << MOVIE_TABLE_NAME << "'..." << std::endl;
        const Aws::DynamoDB::Model::CreateTableOutcome &result = dynamoClient.CreateTable(
                request);
        if (!result.IsSuccess()) {
            if (result.GetError().GetErrorType() ==
                Aws::DynamoDB::DynamoDBErrors::RESOURCE_IN_USE) {
                std::cout << "Table already exists." << std::endl;
                movieTableAlreadyExisted = true;
            }
            else {
                std::cerr << "Failed to create table: "
                          << result.GetError().GetMessage();
                return false;
            }
        }
    }

    // Wait for table to become active.
    if (!movieTableAlreadyExisted) {
        std::cout << "Waiting for table '" << MOVIE_TABLE_NAME
                  << "' to become active...." << std::endl;
        if (!AwsDoc::DynamoDB::waitTableActive(MOVIE_TABLE_NAME, clientConfiguration)) {
            return false;
        }
        std::cout << "Table '" << MOVIE_TABLE_NAME << "' created and active."
                  << std::endl;
    }

    return true;
}
// snippet-end:[cpp.example_code.dynamodb.scenario.createTable]

// snippet-start:[cpp.example_code.dynamodb.scenario.deleteTable]
//! Delete a DynamoDB table.
/*!
  \sa deleteDynamoTable()
  \param tableName: The DynamoDB table's name.
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
*/
bool AwsDoc::DynamoDB::deleteDynamoTable(const Aws::String &tableName,
                                         const Aws::Client::ClientConfiguration& clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::DeleteTableRequest request;
    request.SetTableName(tableName);

    const Aws::DynamoDB::Model::DeleteTableOutcome &result = dynamoClient.DeleteTable(
            request);
    if (result.IsSuccess()) {
        std::cout << "Your Table \""
                  << result.GetResult().GetTableDescription().GetTableName()
                  << " was deleted!\n";
    }
    else {
        std::cerr << "Failed to delete table: " << result.GetError().GetMessage() << std::endl;
    }

    return result.IsSuccess();
}
// snippet-end:[cpp.example_code.dynamodb.scenario.deleteTable]

// snippet-start:[cpp.example_code.dynamodb.scenario.waitTableActive]
//! Query a newly created DynamoDB table until it is active.
/*!
  \sa waitTableActive()
  \param waitTableActive: The DynamoDB table's name.
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
*/
bool AwsDoc::DynamoDB::waitTableActive(const Aws::String &tableName,
                                       const Aws::Client::ClientConfiguration& clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);
    // Repeatedly call DescribeTable until table is ACTIVE.
    const int MAX_QUERIES = 20;
    Aws::DynamoDB::Model::DescribeTableRequest request;
    request.SetTableName(tableName);

    int count = 0;
    while (count < MAX_QUERIES) {
        const Aws::DynamoDB::Model::DescribeTableOutcome &result = dynamoClient.DescribeTable(
                request);
        if (result.IsSuccess()) {
            Aws::DynamoDB::Model::TableStatus status = result.GetResult().GetTable().GetTableStatus();

            if (Aws::DynamoDB::Model::TableStatus::ACTIVE != status) {
                std::this_thread::sleep_for(std::chrono::seconds(1));
            }
            else {
                return true;
            }
        }
        else {
            std::cerr << "Error DynamoDB::waitTableActive "
                      << result.GetError().GetMessage() << std::endl;
            return false;
        }
        count++;
    }
    return false;
}
// snippet-end:[cpp.example_code.dynamodb.scenario.waitTableActive]

//! Command line prompt/response utility function.
/*!
 \\sa askQuestion()
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::DynamoDB::askQuestion(const Aws::String &string,
                                          const std::function<bool(
                                                  Aws::String)> &test) {
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

//! Command line prompt/response utility function for an integer result.
/*!
 \sa askQuestionForInt()
 \param string: A question prompt.
 \return int: User's response.
 */
int AwsDoc::DynamoDB::askQuestionForInt(const Aws::String &string) {
    Aws::String resultString = askQuestion(string,
                                           [](const Aws::String &string1) -> bool {
                                                   try {
                                                       (void)std::stoi(string1);
                                                       return true;
                                                   }
                                                   catch (const std::invalid_argument &) {
                                                       return false;
                                                   }
                                           });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForInt string not an int "
                  << resultString << std::endl;
    }
    return result;
}

//! Command line prompt/response utility function for a float result confined to
//! a range.
/*!
 \sa askQuestionForFloatRange()
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return float: User's response.
 */
float AwsDoc::DynamoDB::askQuestionForFloatRange(const Aws::String &string, float low,
                                                 float high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                float number = std::stof(string1);
                return number >= low && number <= high;
            }
            catch (const std::invalid_argument &) {
                return false;
            }
    });

    float result = 0;
    try {
        result = std::stof(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \sa askQuestionForIntRange()
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int AwsDoc::DynamoDB::askQuestionForIntRange(const Aws::String &string, int low,
                                             int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                return number >= low && number <= high;
            }
            catch (const std::invalid_argument &) {
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

//! Utility function to log movie attributes to std::cout.
/*!
 \sa printMovieInfo()
 \param movieMap: Map of DynamoDB attribute values.
 \return void
 */
void AwsDoc::DynamoDB::printMovieInfo(
        const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &movieMap) {
    {
        auto const &iter = movieMap.find(TITLE_KEY);
        if (iter != movieMap.end()) {
            std::cout << "Movie title: '" + iter->second.GetS() << "'." << std::endl;
        }
    }

    {
        auto const &iter = movieMap.find(YEAR_KEY);
        if (iter != movieMap.end()) {
            std::cout << "    Year: " + iter->second.GetN() << "." << std::endl;
        }
    }

    {
        auto const &iter = movieMap.find(INFO_KEY);
        if (iter != movieMap.end()) {
            Aws::Map<Aws::String, const std::shared_ptr<Aws::DynamoDB::Model::AttributeValue>> infoMap =
                    iter->second.GetM();

            auto const &ratingIter = infoMap.find(RATING_KEY);
            if (ratingIter != infoMap.end()) {
                std::cout << "    Rating: " + ratingIter->second->GetN() << "."
                          << std::endl;
            }

            auto const &plotIter = infoMap.find(PLOT_KEY);
            if (plotIter != infoMap.end()) {
                std::cout << "    Synopsis: " + plotIter->second->GetS() << "."
                          << std::endl;
            }
        }
    }
}
