 
//snippet-sourcedescription:[purge_queue.cpp demonstrates how to delete the messages in an Amazon SQS queue.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


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
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/PurgeQueueRequest.h>
#include <iostream>

/**
 * Purges an sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: purge_queue <queue_url>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_url(argv[1]);

        Aws::SQS::SQSClient sqs;

        Aws::SQS::Model::PurgeQueueRequest pq_req;
        pq_req.SetQueueUrl(queue_url);

        auto pq_out = sqs.PurgeQueue(pq_req);
        if (pq_out.IsSuccess())
        {
            std::cout << "Successfully purged queue " << std::endl;
        }
        else
        {
            std::cout << "Error purging queue " << pq_out.GetError().GetMessage()
                      << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

