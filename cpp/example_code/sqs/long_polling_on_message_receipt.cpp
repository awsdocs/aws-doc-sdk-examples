 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-service:[sqs]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/sqs/model/ReceiveMessageRequest.h>
#include <aws/sqs/model/ReceiveMessageResult.h>
#include <aws/sqs/model/DeleteMessageRequest.h>
#include <iostream>

void ReceiveMessage(const Aws::String& queue_url, int wait_time)
{
    // Let's make sure the request timeout is larger than the maximum possible
    // long poll time so that valid ReceiveMesage requests don't fail on long
    // poll queues
    Aws::Client::ClientConfiguration client_cfg;
    client_cfg.requestTimeoutMs = 30000;

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

