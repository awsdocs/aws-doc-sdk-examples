/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef GLUE_EXAMPLES_GLUE_SAMPLES_H
#define GLUE_EXAMPLES_GLUE_SAMPLES_H
#include <aws/core/Aws.h>
#include <aws/cloudformation/model/Output.h>
#include <functional>

namespace AwsDoc {
    namespace Glue {
        extern const Aws::String CDK_TOOLKIT_STACK_NAME;

        //! Scenario which demonstrates using AWS Glue to add a crawler and run a job.
        /*!
         \\sa runGettingStartedWithGlueScenario()
         \param bucketName: An Amazon Simple Storage Service (Amazon S3) bucket created in the setup.
         \param roleName: An AWS Identity and Access Management (IAM) role created in the setup.
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        bool runGettingStartedWithGlueScenario(const Aws::String &bucketName,
                                               const Aws::String &roleName,
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


        //! Routine to create the AWS CloudFormation resources.
        /*!
         \\sa runGettingStartedWithGlueScenario()
         \param stackName: Name for the CloudFormation stack.
         \param templateFilePath: File path for CloudFormation stack template file.
         \param outputs: Vector to receive the outputs.
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        bool
        createCloudFormationResource(const Aws::String &stackName,
                                     const Aws::String &templateFilePath,
                                     std::vector<Aws::CloudFormation::Model::Output> &outputs,
                                     const Aws::Client::ClientConfiguration &clientConfig);

        bool deleteCloudFormationResource(const Aws::String &stackName,
                                          const Aws::Client::ClientConfiguration &clientConfig);


        Aws::CloudFormation::Model::Stack
        getStackDescription(const Aws::String &stackName,
                            const Aws::Client::ClientConfiguration &clientConfig);

        bool bootstrapCDK(bool &cdkBootstrapCreated,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool getRoleArn(const Aws::String &roleName,
                        Aws::String &roleArn,
                        const Aws::Client::ClientConfiguration &clientConfig);
    } // Glue
} // AwsDoc

#endif //GLUE_EXAMPLES_GLUE_SAMPLES_H
