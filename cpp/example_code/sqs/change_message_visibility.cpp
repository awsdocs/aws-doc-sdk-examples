//snippet-sourcedescription:[change_message_visibility.cpp demonstrates how to change the visibility timeout of a message in an Amazon SQS queue.]
//snippet-service:[sqs]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
//snippet-start:[sqs.cpp.change_message_visibility.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ChangeMessageVisibilityRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/model/ReceiveMessageResult.h>
#include <iostream>
//snippet-end:[sqs.cpp.change_message_visibility.inc]

void ChangeMessageVisibility(
    const Aws::String& queue_url, int visibility_timeout)
{
    // Let's make sure the request timeout is larger than the maximum possible
    // long poll time so that valid ReceiveMessage requests don't fail on long
    // poll queues
    Aws::Client::ClientConfiguration client_config;
    client_config.requestTimeoutMs = 30000;

    Aws::SQS::SQSClient sqs(client_config);

    Aws::SQS::Model::ReceiveMessageRequest receive_request;
    receive_request.SetQueueUrl(queue_url);
    receive_request.SetMaxNumberOfMessages(1);

    auto receive_outcome = sqs.ReceiveMessage(receive_request);
    if (!receive_outcome.IsSuccess())
    {
        std::cout << "Error receiving message from queue " << queue_url << ": "
            << receive_outcome.GetError().GetMessage() << std::endl;
        return;
    }

    const auto& messages = receive_outcome.GetResult().GetMessages();
    if (messages.size() == 0)
    {
        std::cout << "No messages received from queue " << queue_url <<
            std::endl;
        return;
    }

    const auto& message = messages[0];
    std::cout << "Received message:" << std::endl;
    std::cout << "  MessageId: " << message.GetMessageId() << std::endl;
    std::cout << "  ReceiptHandle: " << message.GetReceiptHandle() << std::endl;
    std::cout << "  Body: " << message.GetBody() << std::endl << std::endl;

    // snippet-start:[sqs.cpp.change_message_visibility.code]
    Aws::SQS::Model::ChangeMessageVisibilityRequest request;
    request.SetQueueUrl(queue_url);
    request.SetReceiptHandle(message.GetReceiptHandle());
    request.SetVisibilityTimeout(visibility_timeout);
    auto outcome = sqs.ChangeMessageVisibility(request);
    if (outcome.IsSuccess())
    {
        std::cout << "Successfully changed visibility of message " <<
            message.GetMessageId() << " from queue " << queue_url << std::endl;
    }
    else
    {
        std::cout << "Error changing visibility of message " <<
            message.GetMessageId() << " from queue " << queue_url << ": " <<
            outcome.GetError().GetMessage() << std::endl;
    }
    // snippet-end:[sqs.cpp.change_message_visibility.code]
}

/**
 * Changes the visibility timeout of a message received from an SQS queue, based
 * on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: change_message_visibility <queue_url> " <<
            "<visibility_timeout_in_seconds>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_url = argv[1];
        int visibility_timeout = 0;
        Aws::StringStream ss(argv[2]);
        ss >> visibility_timeout;
        ChangeMessageVisibility(queue_url, visibility_timeout);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

