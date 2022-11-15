/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef DYNAMODB_EXAMPLES_DYANAMODB_SAMPLES_H
#define DYNAMODB_EXAMPLES_DYANAMODB_SAMPLES_H

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
        extern const int ASTERIX_FILL_WIDTH;

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

        //! Create a DynamoDB table.
        /*!
          \sa createDynamoDBTable()
          \param tableName: The DynamoDB table's name.
          \param clientConfiguration: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool createDynamoDBTable(const Aws::String &tableName,
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
#endif //DYNAMODB_EXAMPLES_DYANAMODB_SAMPLES_H
