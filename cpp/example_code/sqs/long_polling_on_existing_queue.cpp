//snippet-sourcedescription:[long_polling_on_existing_queue.cpp demonstrates how to change the amount of time an Amazon SQS queue waits for a message to arrive.]
//snippet-service:[sqs]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-keyword:[C++]
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

