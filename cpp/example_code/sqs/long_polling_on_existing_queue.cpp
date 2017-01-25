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
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <iostream>

/**
 * Modifies an sqs queue to have a long poll wait time, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3) {
        std::cout << "Usage: long_polling_on_existing_queue <queue_url> " <<
            "<long_poll_time_in_seconds>" << std::endl;
        return 1;
    }

    Aws::String queue_url = argv[1];
    Aws::String poll_time = argv[2];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::SQS::SQSClient sqs;

        Aws::SQS::Model::SetQueueAttributesRequest sqa_req;
        sqa_req.SetQueueUrl(queue_url);
        sqa_req.AddAttributes(
                Aws::SQS::Model::QueueAttributeName::ReceiveMessageWaitTimeSeconds,
                poll_time);

        auto sqa_out = sqs.SetQueueAttributes(sqa_req);
        if (sqa_out.IsSuccess())
        {
            std::cout << "Successfully updated long polling time for queue " <<
            queue_url << " to " << poll_time << std::endl;
        }
        else
        {
            std::cout << "Error updating long polling time for queue " << queue_url
            << ": " << sqa_out.GetError().GetMessage() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}

