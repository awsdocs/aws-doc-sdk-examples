/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ChangeMessageVisibilityRequest.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/model/ReceiveMessageResult.h>

#include <iostream>

void ChangeMessageVisibility(const Aws::String& queueUrl, int visibilityTimeout)
{
    // Let's make sure the request timeout is larger than the maximum possible long poll time so that
    // valid ReceiveMesage requests don't fail on long poll queues
    Aws::Client::ClientConfiguration clientConfig;
    clientConfig.requestTimeoutMs = 30000;

    Aws::SQS::SQSClient sqs_client(clientConfig);

    Aws::SQS::Model::ReceiveMessageRequest receiveMessageRequest;
    receiveMessageRequest.SetQueueUrl(queueUrl);
    receiveMessageRequest.SetMaxNumberOfMessages(1);
    auto receiveMessageOutcome = sqs_client.ReceiveMessage(receiveMessageRequest);
    if(!receiveMessageOutcome.IsSuccess())
    {
        std::cout << "Error receiving message from queue " << queueUrl << ": " << receiveMessageOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    const auto& messages = receiveMessageOutcome.GetResult().GetMessages();
    if(messages.size() == 0)
    {
        std::cout << "No messages received from queue " << queueUrl << std::endl;
        return;
    }

    const auto& message = messages[0];
    std::cout << "Received message:" << std::endl;
    std::cout << "  MessageId: " << message.GetMessageId() << std::endl;
    std::cout << "  ReceiptHandle: " << message.GetReceiptHandle() << std::endl;
    std::cout << "  Body: " << message.GetBody() << std::endl << std::endl;

    Aws::SQS::Model::ChangeMessageVisibilityRequest changeMessageVisibilityRequest;
    changeMessageVisibilityRequest.SetQueueUrl(queueUrl);
    changeMessageVisibilityRequest.SetReceiptHandle(message.GetReceiptHandle());
    changeMessageVisibilityRequest.SetVisibilityTimeout(visibilityTimeout);
    auto changeMessageVisibilityOutcome = sqs_client.ChangeMessageVisibility(changeMessageVisibilityRequest);
    if(changeMessageVisibilityOutcome.IsSuccess())
    {
        std::cout << "Successfully changed visibility of message " << message.GetMessageId() << " from queue " << queueUrl << std::endl;
    }
    else
    {
        std::cout << "Error changing visibility of message " << message.GetMessageId() << " from queue " << queueUrl << ": " << changeMessageVisibilityOutcome.GetError().GetMessage() << std::endl;
    }
}

/**
 * Changes the visibility timeout of a message received from an sqs queue, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: sqs_change_message_visibility <queue_url> <visibility_timeout_in_seconds>" << std::endl;
        return 1;
    }

    Aws::String queueUrl = argv[1];

    int visibilityTimeoutInSeconds = 0;
    Aws::StringStream ss(argv[2]);
    ss >> visibilityTimeoutInSeconds;

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    ChangeMessageVisibility(queueUrl, visibilityTimeoutInSeconds);

    Aws::ShutdownAPI(options);

    return 0;
}



