/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <iostream>
#include "sqs_samples.h"

// snippet-start:[cpp.example_code.sqs.SetQueueAttributes]
//! Set the value for an attribute in an Amazon Simple Queue Service (Amazon SQS) queue.
/*!
  \param queueUrl: An Amazon SQS queue URL.
  \param attributeName: An attribute name enum.
  \param attribute: The attribute value as a string.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SQS::setQueueAttributes(const Aws::String &queueURL,
                                     Aws::SQS::Model::QueueAttributeName attributeName,
                                     const Aws::String &attribute,
                                     const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    Aws::SQS::Model::SetQueueAttributesRequest request;
    request.SetQueueUrl(queueURL);
    request.AddAttributes(
            attributeName,
            attribute);

    const Aws::SQS::Model::SetQueueAttributesOutcome outcome = sqsClient.SetQueueAttributes(
            request);
    if (outcome.IsSuccess()) {
        std::cout << "Successfully set the attribute  " <<
                  Aws::SQS::Model::QueueAttributeNameMapper::GetNameForQueueAttributeName(
                          attributeName)
                  << " with value " << attribute << " in queue " <<
                  queueURL << "." << std::endl;
    }
    else {
        std::cout << "Error setting attribute for  queue " <<
                  queueURL << ": " << outcome.GetError().GetMessage() <<
                  std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.sqs.SetQueueAttributes]

/*
 *
 *  main function
 *
 *  Usage: 'run_set_queue_attributes <source_queue_url> <attribute_name> <attribute_value>'
 *
 *  Prerequisites: An existing Amazon SQS queue.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 4) {
        std::cout << "Usage: run_set_queue_attributes <source_queue_url> " <<
                  "<attribute_name> <attribute_value>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queueUrl = argv[1];
        Aws::String attributeStr = argv[2];

        Aws::String attributeValue = argv[3];

        Aws::SQS::Model::QueueAttributeName attributeName =
                Aws::SQS::Model::QueueAttributeNameMapper::GetQueueAttributeNameForName(
                        attributeStr);

        if (attributeName == Aws::SQS::Model::QueueAttributeName::NOT_SET) {
            std::cerr << "Invalid attribute '" << attributeStr << "'." << std::endl;
            return 1;
        }

        // snippet-start:[cpp.example_code.sqs.SetQueueAttributes.config]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.sqs.SetQueueAttributes.config]

        AwsDoc::SQS::setQueueAttributes(queueUrl, attributeName,
                                        attributeValue, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
