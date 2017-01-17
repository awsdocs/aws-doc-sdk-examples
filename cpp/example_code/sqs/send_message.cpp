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
#include <aws/sqs/model/SendMessageRequest.h>
#include <aws/sqs/model/SendMessageResult.h>

#include <iostream>

/**
 * Sends a message to an sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: sqs_send_message <queue_url> <message_body>" << std::endl;
        return 1;
    }

    Aws::String queueUrl = argv[1];
    Aws::String messageBody = argv[2];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::SQS::SQSClient sqs_client;

    Aws::SQS::Model::SendMessageRequest sendMessageRequest;
    sendMessageRequest.SetQueueUrl(queueUrl);
    sendMessageRequest.SetMessageBody(messageBody);
    auto sendMessageOutcome = sqs_client.SendMessage(sendMessageRequest);
    if(sendMessageOutcome.IsSuccess())
    {
        std::cout << "Successfully sent message to " << queueUrl << std::endl;
    }
    else
    {
        std::cout << "Error sending message to " << queueUrl << ": " << sendMessageOutcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



