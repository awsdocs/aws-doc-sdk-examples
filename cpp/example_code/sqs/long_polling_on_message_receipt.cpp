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

//snippet-start:[sqs.cpp.long_polling_on_message_receipt.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
//snippet-end:[sqs.cpp.long_polling_on_message_receipt.inc]
#include <aws/sqs/model/DeleteMessageRequest.h>
#include <iostream>
#include "sqs_samples.h"

//! Receive a message from an Amazon Simple Queue Service (Amazon SQS) queue
//! that specifies the wait time.
/*!
  \param queueUrl: An Amazon SQS queue URL.
  \param waitTimeSeconds: The wait time in seconds.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SQS::receiveMessageWithWaitTime(const Aws::String &queueUrl,
                                             int waitTimeSeconds,
                                             const Aws::Client::ClientConfiguration &clientConfiguration) {
    // Make sure the request timeout is larger than the maximum possible
    // long poll time so that valid receiveMessage requests don't fail on long
    // poll queues.
    Aws::Client::ClientConfiguration customConfiguration(clientConfiguration);
    customConfiguration.requestTimeoutMs = 30000;

    // snippet-start:[sqs.cpp.long_polling_on_message_receipt.code]
    Aws::SQS::SQSClient sqsClient(customConfiguration);

    Aws::SQS::Model::ReceiveMessageRequest request;
    request.SetQueueUrl(queueUrl);
    request.SetMaxNumberOfMessages(1);
    request.SetWaitTimeSeconds(waitTimeSeconds);

    auto outcome = sqsClient.ReceiveMessage(request);
    if (outcome.IsSuccess()) {
        const auto &messages = outcome.GetResult().GetMessages();
        if (messages.empty()) {
            std::cout << "No messages received from queue " << queueUrl <<
                      std::endl;
        }
        else {
            const auto &message = messages[0];
            std::cout << "Received message:" << std::endl;
            std::cout << "  MessageId: " << message.GetMessageId() << std::endl;
            std::cout << "  ReceiptHandle: " << message.GetReceiptHandle() << std::endl;
            std::cout << "  Body: " << message.GetBody() << std::endl << std::endl;
        }
    }
    else {
        std::cout << "Error receiving message from queue " << queueUrl << ": "
                  << outcome.GetError().GetMessage() << std::endl;
    }
    // snippet-end:[sqs.cpp.long_polling_on_message_receipt.code]

    return outcome.IsSuccess();
}

/*
 *
 *  main function
 *
 *  Usage: 'run_long_polling_on_message_receipt <queue_url> <long_poll_time_in_seconds>'
 *
 *  Prerequisites: An existing Amazon SQS queue.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_long_polling_on_message_receipt <queue_url> " <<
                  "<long_poll_time_in_seconds>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queueUrl = argv[1];

        int waitTime = 1;
        Aws::StringStream ss(argv[2]);
        ss >> waitTime;

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SQS::receiveMessageWithWaitTime(queueUrl, waitTime, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD


