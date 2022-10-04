/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
#pragma once
#ifndef DYNAMODB_EXAMPLES_DYNAMODB_UTILITIES_H
#define DYNAMODB_EXAMPLES_DYNAMODB_UTILITIES_H

#include <aws/core/Aws.h>
#include <aws/dynamodb/model/AttributeValue.h>
namespace AwsDoc {
    namespace DynamoDB {
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

        //! Download a JSON movie database file from the web and unzip the file.
        /*!
         \sa getMovieJSON()
         \return Aws::String: Movie database as JSON string.
         */
        Aws::String getMovieJSON();

    } //  namespace DynamoDB
} // namespace AwsDoc

#endif //DYNAMODB_EXAMPLES_DYNAMODB_UTILITIES_H
