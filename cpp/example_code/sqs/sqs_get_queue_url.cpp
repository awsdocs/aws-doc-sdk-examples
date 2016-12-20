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
#include <aws/sqs/model/GetQueueUrlRequest.h>
#include <aws/sqs/model/GetQueueUrlResult.h>

#include <iostream>

/**
 * Gets the url associated with an sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: sqs_get_queue_url <queue_name>" << std::endl;
        return 1;
    }

    Aws::String queueName = argv[1];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::SQS::SQSClient sqs_client;

    Aws::SQS::Model::GetQueueUrlRequest getQueueUrlRequest;
    getQueueUrlRequest.SetQueueName(queueName);
    auto getQueueUrlOutcome = sqs_client.GetQueueUrl(getQueueUrlRequest);
    if(getQueueUrlOutcome.IsSuccess())
    {
        std::cout << "Queue " << queueName << " has url " << getQueueUrlOutcome.GetResult().GetQueueUrl() << std::endl;
    }
    else
    {
        std::cout << "Error getting url for queue " << queueName << ": " << getQueueUrlOutcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



