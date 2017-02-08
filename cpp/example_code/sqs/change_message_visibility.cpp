/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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

void ChangeMessageVisibility(
        const Aws::String& queue_url, int visibility_timeout)
{
    // Let's make sure the request timeout is larger than the maximum possible
    // long poll time so that valid ReceiveMesage requests don't fail on long
    // poll queues
    Aws::Client::ClientConfiguration client_config;
    client_config.requestTimeoutMs = 30000;

    Aws::SQS::SQSClient sqs(client_config);

    Aws::SQS::Model::ReceiveMessageRequest rm_req;
    rm_req.SetQueueUrl(queue_url);
    rm_req.SetMaxNumberOfMessages(1);

    auto rm_out = sqs.ReceiveMessage(rm_req);
    if (!rm_out.IsSuccess()) {
        std::cout << "Error receiving message from queue " << queue_url << ": "
            << rm_out.GetError().GetMessage() << std::endl;
        return;
    }

    const auto& messages = rm_out.GetResult().GetMessages();
    if (messages.size() == 0) {
        std::cout << "No messages received from queue " << queue_url <<
            std::endl;
        return;
    }

    const auto& message = messages[0];
    std::cout << "Received message:" << std::endl;
    std::cout << "  MessageId: " << message.GetMessageId() << std::endl;
    std::cout << "  ReceiptHandle: " << message.GetReceiptHandle() << std::endl;
    std::cout << "  Body: " << message.GetBody() << std::endl << std::endl;

    Aws::SQS::Model::ChangeMessageVisibilityRequest cmv_req;
    cmv_req.SetQueueUrl(queue_url);
    cmv_req.SetReceiptHandle(message.GetReceiptHandle());
    cmv_req.SetVisibilityTimeout(visibility_timeout);
    auto cmv_out = sqs.ChangeMessageVisibility(cmv_req);
    if (cmv_out.IsSuccess()) {
        std::cout << "Successfully changed visibility of message " <<
            message.GetMessageId() << " from queue " << queue_url << std::endl;
    } else {
        std::cout << "Error changing visibility of message " <<
            message.GetMessageId() << " from queue " << queue_url << ": " <<
            cmv_out.GetError().GetMessage() << std::endl;
    }
}

/**
 * Changes the visibility timeout of a message received from an sqs queue, based
 * on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3) {
        std::cout << "Usage: change_message_visibility <queue_url> " <<
            "<visibility_timeout_in_seconds>" << std::endl;
        return 1;
    }

    Aws::String queue_url = argv[1];

    int visibility_timeout = 0;
    Aws::StringStream ss(argv[2]);
    ss >> visibility_timeout;

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    ChangeMessageVisibility(queue_url, visibility_timeout);

    Aws::ShutdownAPI(options);

    return 0;
}

