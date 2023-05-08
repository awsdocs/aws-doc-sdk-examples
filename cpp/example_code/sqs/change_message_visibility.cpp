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

//snippet-start:[sqs.cpp.change_message_visibility.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ChangeMessageVisibilityRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.change_message_visibility.inc]
#include "sqs_samples.h"

// snippet-start:[cpp.example_code.sqs.ChangeMessageVisibility]
//! Changes the visibility timeout of a message in an Amazon Simple Queue Service
//! (Amazon SQS) queue.
/*!
  \param queueUrl: An Amazon SQS queue URL.
  \param messageReceiptHandle: A message receipt handle.
  \param visibilityTimeoutSeconds: Visibility timeout in seconds.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SQS::changeMessageVisibility(
        const Aws::String &queue_url,
        const Aws::String &messageReceiptHandle,
        int visibilityTimeoutSeconds,
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    // snippet-start:[sqs.cpp.change_message_visibility.code]
    Aws::SQS::Model::ChangeMessageVisibilityRequest request;
    request.SetQueueUrl(queue_url);
    request.SetReceiptHandle(messageReceiptHandle);
    request.SetVisibilityTimeout(visibilityTimeoutSeconds);

    auto outcome = sqsClient.ChangeMessageVisibility(request);
    if (outcome.IsSuccess()) {
        std::cout << "Successfully changed visibility of message " <<
                  messageReceiptHandle << " from queue " << queue_url << std::endl;
    }
    else {
        std::cout << "Error changing visibility of message from queue "
                  << queue_url << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    // snippet-end:[sqs.cpp.change_message_visibility.code]

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.sqs.ChangeMessageVisibility]

/*
 *
 *  main function
 *
 *  Usage: 'run_change_message_visibility <queue_url> <message_receipt_handle>
 *          <visibility_timeout_in_seconds>'
 *
 *  Prerequisites: An existing Amazon SQS queue and a message in the queue.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 4) {
        std::cout << "Usage: run_change_message_visibility <queue_url> " <<
                  "<message_receipt_handle> <visibility_timeout_in_seconds>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queueUrl = argv[1];
        Aws::String messageReceiptHandle = argv[2];

        int visibilityTimeout = 0;
        Aws::StringStream ss(argv[3]);
        ss >> visibilityTimeout;

        // snippet-start:[cpp.example_code.sqs.ChangeMessageVisibility.config]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.sqs.ChangeMessageVisibility.config]

        AwsDoc::SQS::changeMessageVisibility(queueUrl,
                                             messageReceiptHandle,
                                             visibilityTimeout,
                                             clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

