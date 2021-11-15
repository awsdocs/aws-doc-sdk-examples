/*
Purpose:
long_polling_on_message_receipt.cpp demonstrates how to retrieve messages from an Amazon SQS queue using long-poll support.




// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
//snippet-start:[sqs.cpp.long_polling_on_message_receipt.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/model/ReceiveMessageResult.h>
//snippet-end:[sqs.cpp.long_polling_on_message_receipt.inc]
#include <aws/sqs/model/DeleteMessageRequest.h>
#include <iostream>

void ReceiveMessage(const Aws::String& queue_url, int wait_time)
{
    // Let's make sure the request timeout is larger than the maximum possible
    // long poll time so that valid ReceiveMessage requests don't fail on long
    // poll queues
    Aws::Client::ClientConfiguration client_cfg;
    client_cfg.requestTimeoutMs = 30000;

    // snippet-start:[sqs.cpp.long_polling_on_message_receipt.code]
    Aws::SQS::SQSClient sqs(client_cfg);

    Aws::SQS::Model::ReceiveMessageRequest request;
    request.SetQueueUrl(queue_url);
    request.SetMaxNumberOfMessages(1);
    request.SetWaitTimeSeconds(wait_time);

    auto outcome = sqs.ReceiveMessage(request);
    if (!outcome.IsSuccess())
    {
        std::cout << "Error receiving message from queue " << queue_url << ": "
            << outcome.GetError().GetMessage() << std::endl;
        return;
    }

    const auto& messages = outcome.GetResult().GetMessages();
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
    // snippet-end:[sqs.cpp.long_polling_on_message_receipt.code]

    Aws::SQS::Model::DeleteMessageRequest delete_request;
    delete_request.SetQueueUrl(queue_url);
    delete_request.SetReceiptHandle(message.GetReceiptHandle());

    auto delete_outcome = sqs.DeleteMessage(delete_request);
    if (delete_outcome.IsSuccess())
    {
        std::cout << "Successfully deleted message " << message.GetMessageId()
            << " from queue " << queue_url << std::endl;
    }
    else
    {
        std::cout << "Error deleting message " << message.GetMessageId() <<
            " from queue " << queue_url << ": " <<

            delete_outcome.GetError().GetMessage() << std::endl;
    }
}

/**
 * Receives (and deletes) a message from an sqs queue via long polling, based on
 * command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: long_polling_on_message_receipt <queue_url> " <<
            "<long_poll_time_in_seconds>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_url = argv[1];

        int wait_time = 1;
        Aws::StringStream ss(argv[2]);
        ss >> wait_time;

        ReceiveMessage(queue_url, wait_time);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

