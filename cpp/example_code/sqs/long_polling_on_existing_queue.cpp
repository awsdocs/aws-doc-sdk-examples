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

//snippet-start:[sqs.cpp.long_polling_on_existing-queue.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.long_polling_on_existing-queue.inc]
#include "sqs_samples.h"

// snippet-start:[cpp.example_code.sqs.LongPollingAttribute]
//! Set the wait time for an Amazon Simple Queue Service (Amazon SQS) queue poll.
/*!
  \param queueUrl: An Amazon SQS queue URL.
  \param pollTimeSeconds: The receive message wait time in seconds.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SQS::setQueueLongPollingAttribute(const Aws::String &queueURL,
                                               const Aws::String &pollTimeSeconds,
                                               const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[sqs.cpp.long_polling_on_existing-queue.code]
    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    Aws::SQS::Model::SetQueueAttributesRequest request;
    request.SetQueueUrl(queueURL);
    request.AddAttributes(
            Aws::SQS::Model::QueueAttributeName::ReceiveMessageWaitTimeSeconds,
            pollTimeSeconds);

    const Aws::SQS::Model::SetQueueAttributesOutcome outcome = sqsClient.SetQueueAttributes(
            request);
    if (outcome.IsSuccess()) {
        std::cout << "Successfully updated long polling time for queue " <<
                  queueURL << " to " << pollTimeSeconds << std::endl;
    }
    else {
        std::cout << "Error updating long polling time for queue " <<
                  queueURL << ": " << outcome.GetError().GetMessage() <<
                  std::endl;
    }
    // snippet-end:[sqs.cpp.long_polling_on_existing-queue.code]

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.sqs.LongPollingAttribute]

/*
 *
 *  main function
 *
 *  Usage: 'run_long_polling_on_existing_queue <queue_url> <long_poll_time_in_seconds>'
 *
 *  Prerequisites: An existing Amazon SQS queue.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_long_polling_on_existing_queue <queue_url> " <<
                  "<long_poll_time_in_seconds>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queueUrl = argv[1];
        Aws::String pollTime = argv[2];

    // snippet-start:[cpp.example_code.sqs.LongPollingAttribute.config]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
    // snippet-end:[cpp.example_code.sqs.LongPollingAttribute.config]

        AwsDoc::SQS::setQueueLongPollingAttribute(queueUrl, pollTime, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
