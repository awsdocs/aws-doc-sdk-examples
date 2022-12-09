/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef DYNAMODB_EXAMPLES_DYNAMODB_SAMPLES_H
#define DYNAMODB_EXAMPLES_DYNAMODB_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>
#include <aws/dynamodb/model/AttributeValue.h>

namespace AwsDoc {
    namespace DynamoDB {
        /**
        * Constants for DynamoDB table creation and access.
        */
        extern const Aws::String MOVIE_TABLE_NAME;
        extern const Aws::String YEAR_KEY;
        extern const Aws::String TITLE_KEY;
        extern const Aws::String INFO_KEY;
        extern const Aws::String RATING_KEY;
        extern const Aws::String PLOT_KEY;
        extern const int PROVISIONED_THROUGHPUT_UNITS;
        extern const Aws::String ALLOCATION_TAG;
        extern const int ASTERISK_FILL_WIDTH;

        //! Scenario to modify and query a DynamoDB table using single PartiQL statements.
        /*!
          \sa partiqlExecuteScenario()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool partiqlExecuteScenario(
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Scenario to modify and query a DynamoDB table using PartiQL batch statements.
        /*!
          \sa partiqlBatchExecuteScenario()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool partiqlBatchExecuteScenario(
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Scenario to modify and query a DynamoDB table.
        /*!
          \sa dynamodbGettingStartedScenario()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool dynamodbGettingStartedScenario(
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Batch get items from different Amazon DynamoDB tables.
        /*!
          \sa batchGetItem()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool batchGetItem(const Aws::Client::ClientConfiguration &clientConfiguration);

         //! Create an DynamoDB table.
        /*!
          \sa createDynamoDBTable()
          \param tableName: Name for the DynamoDB table.
          \param primaryKey: Primary key for the DynamoDB table.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool createDynamoDBTable(const Aws::String &tableName,
                                 const Aws::String& primaryKey,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an DynamoDB table with a composite key.
        /*!
          \sa createDynamoDBTableWithCompositeKey()
          \param tableName: Name for the DynamoDB table.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool createDynamoDBTableWithCompositeKey(const Aws::String &tableName,
                                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a DynamoDB table.
        /*!
          \sa deleteDynamoTable()
          \param tableName: The DynamoDB table's name.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool deleteDynamoTable(const Aws::String &tableName,
                               const Aws::Client::ClientConfiguration &clientConfiguration);


        //! Deletes an item from a DynamoDB table.
        /*!
          \sa deleteItem()
          \param tableName: The table name.
          \param partitionKey: The partition key.
          \param partitionValue: The value for the partition key.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */

        bool deleteItem(const Aws::String& tableName,
                        const Aws::String& partitionKey,
                        const Aws::String& partitionValue,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe a DynamoDB table.
        /*!
          \sa describeTable()
          \param tableName: The DynamoDB table's name.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool describeTable(const Aws::String &tableName,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Gets an item from a DynamoDB table.
        /*!
          \sa getItem()
          \param tableName: The table name.
          \param partitionKey: The partition key.
          \param partitionValue: The value for the partition key.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */

        bool getItem(const Aws::String& tableName,
                        const Aws::String& partitionKey,
                        const Aws::String& partitionValue,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the DynamoDB tables for the current AWS account.
        /*!
          \sa getItem()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */

        bool listTables(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Put an item in a DynamoDB table.
        /*!
          \sa putItem()
          \param tableName: The table name.
          \param artistKey: The artist key. This is the partition key for the table.
          \param artistValue: The artist value.
          \param albumTitleKey: The album title key.
          \param albumTitleValue: The album title value.
          \param awardsKey: The awards key.
          \param awardsValue: The awards value.
          \param songTitleKey: The song title key.
          \param songTitleValue: The song title value.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
         */
        bool putItem(const Aws::String& tableName,
                     const Aws::String& artistKey,
                     const Aws::String& artistValue,
                     const Aws::String& albumTitleKey,
                     const Aws::String& albumTitleValue,
                     const Aws::String& awardsKey,
                     const Aws::String& awardsValue,
                     const Aws::String& songTitleKey,
                     const Aws::String& songTitleValue,
                     const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Perform  a query on a DynamoDB Table and retrieve items.
        /*!
          \sa queryItem()
          \param tableName: The table name.
          \param partitionKey: The partition key.
          \param partitionValue: The value for the partition key.
          \param projectionExpression: The projections expression, which is ignored if empty.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
          */
        bool queryItem(const Aws::String& tableName,
                       const Aws::String& partitionKey,
                       const Aws::String& partitionValue,
                       const Aws::String& projectionExpression,
                       const Aws::Client::ClientConfiguration &clientConfiguration);


        //! Create a DynamoDB table to be used in sample code scenarios.
        /*!
          \sa createMoviesDynamoDBTable()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool createMoviesDynamoDBTable(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete the DynamoDB table used for sample code scenarios.
        /*!
          \sa deleteMoviesDynamoDBTable()
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool deleteMoviesDynamoDBTable(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Query a newly created DynamoDB table until it is active.
        /*!
          \sa waitTableActive()
          \param waitTableActive: The DynamoDB table's name.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool waitTableActive(const Aws::String &tableName,
                             const Aws::Client::ClientConfiguration &clientConfiguration);
        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        Aws::String askQuestion(const Aws::String &string,
                                const std::function<bool(
                                        Aws::String)> &test = [](
                                        const Aws::String &) -> bool { return true; });

        //! Command line prompt/response utility function for an integer result.
        /*!
         \sa askQuestionForInt()
         \param string: A question prompt.
         \return int: User's response.
         */
        int askQuestionForInt(const std::string &string);

        //! Command line prompt/response utility function for a float result confined to
        //! a range.
        /*!
         \sa askQuestionForFloatRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return float: User's response.
         */
        float
        askQuestionForFloatRange(const Aws::String &string, float low, float high);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);

        //! Utility function to log movie attributes to std::cout.
        /*!
         \sa printMovieInfo()
         \param movieMap: Map of DynamoDB attribute values.
         \return void
         */
        void printMovieInfo(
                const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &movieMap);

    } // DynamoDB
} // AwsDoc
#endif //DYNAMODB_EXAMPLES_DYNAMODB_SAMPLES_H
