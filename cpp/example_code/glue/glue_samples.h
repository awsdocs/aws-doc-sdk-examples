/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef GLUE_EXAMPLES_GLUE_SAMPLES_H
#define GLUE_EXAMPLES_GLUE_SAMPLES_H

#include <aws/core/Aws.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/cloudformation/model/Output.h>
#include <functional>

namespace AwsDoc {
    namespace Glue {

        //! Scenario which demonstrates using AWS Glue to add a crawler and run a job.
        /*!
         \\sa runGettingStartedWithGlueScenario()
         \param bucketName: An Amazon Simple Storage Service (Amazon S3) bucket created in the setup.
         \param roleName: An AWS Identity and Access Management (IAM) role created in the setup.
         \param clientConfig: AWS client configuration.
         \return bool: Successful completion.
         */
        bool runGettingStartedWithGlueScenario(const Aws::String &bucketName,
                                               const Aws::String &roleName,
                                               const Aws::Client::ClientConfiguration &clientConfig);


        //! Routine that retrieves the Amazon Resource Name (ARN) for an IAM role.
        /*!
         \\sa getRoleArn()
         \param roleName: An IAM role name.
         \param roleName: A string to receive the role ARN.
         \param clientConfig: AWS client configuration.
         \return bool: Successful completion.
         */
        bool getRoleArn(const Aws::String &roleName, Aws::String &roleArn,
                        const Aws::Client::ClientConfiguration &clientConfig);

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
    } // Glue
} // AwsDoc

#endif //GLUE_EXAMPLES_GLUE_SAMPLES_H
