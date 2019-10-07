 
//snippet-sourcedescription:[long_polling_on_create_queue.cpp demonstrates how to create an Amazon SQS queue that waits for a message to arrive.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-service:[sqs]
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
//snippet-start:[sqs.cpp.long_polling_on_create_queue.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/CreateQueueRequest.h>
#include <aws/sqs/model/CreateQueueResult.h>
#include <iostream>
//snippet-end:[sqs.cpp.long_polling_on_create_queue.inc]

/**
 * Creates a long-polled sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: long_polling_on_create_queue <queue_name> " <<
            "<poll_time_in_seconds>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_name = argv[1];
        Aws::String poll_time = argv[2];

        // snippet-start:[sqs.cpp.long_polling_on_create_queue.code]
        Aws::SQS::SQSClient sqs;

        Aws::SQS::Model::CreateQueueRequest request;
        request.SetQueueName(queue_name);
        request.AddAttributes(
            Aws::SQS::Model::QueueAttributeName::ReceiveMessageWaitTimeSeconds,
            poll_time);

        auto outcome = sqs.CreateQueue(request);
        if (outcome.IsSuccess())
        {
            std::cout << "Successfully created queue " << queue_name <<
                std::endl;
        }
        else
        {
            std::cout << "Error creating queue " << queue_name << ": " <<
                outcome.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[sqs.cpp.long_polling_on_create_queue.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

