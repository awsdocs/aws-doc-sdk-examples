 
//snippet-sourcedescription:[create_queue.cpp demonstrates how to create an Amazon SQS standard queue.]
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
//snippet-start:[sqs.cpp.create_queue.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/CreateQueueRequest.h>
#include <aws/sqs/model/CreateQueueResult.h>
#include <iostream>
//snippet-end:[sqs.cpp.create_queue.inc]

/**
 * Creates an sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: create_queue <queue_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_name = argv[1];

        // snippet-start:[sqs.cpp.create_queue.code]
        Aws::SQS::SQSClient sqs;

        Aws::SQS::Model::CreateQueueRequest cq_req;
        cq_req.SetQueueName(queue_name);

        auto cq_out = sqs.CreateQueue(cq_req);
        if (cq_out.IsSuccess())
        {
            std::cout << "Successfully created queue " << queue_name << std::endl;
        }
        else
        {
            std::cout << "Error creating queue " << queue_name << ": " <<
                cq_out.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[sqs.cpp.create_queue.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

