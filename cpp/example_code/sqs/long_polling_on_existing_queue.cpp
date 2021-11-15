/*
Purpose:
long_polling_on_existing_queue.cpp demonstrates how to change the amount of time an Amazon SQS queue waits for a message to arrive.




// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
//snippet-start:[sqs.cpp.long_polling_on_existing-queue.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.long_polling_on_existing-queue.inc]

/**
 * Modifies an sqs queue to have a long poll wait time, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: long_polling_on_existing_queue <queue_url> " <<
            "<long_poll_time_in_seconds>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_url = argv[1];
        Aws::String poll_time = argv[2];

        // snippet-start:[sqs.cpp.long_polling_on_existing-queue.code]
        Aws::SQS::SQSClient sqs;

        Aws::SQS::Model::SetQueueAttributesRequest request;
        request.SetQueueUrl(queue_url);
        request.AddAttributes(
            Aws::SQS::Model::QueueAttributeName::ReceiveMessageWaitTimeSeconds,
            poll_time);

        auto outcome = sqs.SetQueueAttributes(request);
        if (outcome.IsSuccess())
        {
            std::cout << "Successfully updated long polling time for queue " <<
                queue_url << " to " << poll_time << std::endl;
        }
        else
        {
            std::cout << "Error updating long polling time for queue " <<
                queue_url << ": " << outcome.GetError().GetMessage() <<
                std::endl;
        }
        // snippet-end:[sqs.cpp.long_polling_on_existing-queue.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

