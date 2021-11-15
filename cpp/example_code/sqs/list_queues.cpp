
/*
Purpose:
list_queues.cpp demonstrates how to retrieve a list of Amazon SQS queues for an AWS account.





// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
//snippet-start:[sqs.cpp.list_queues.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ListQueuesRequest.h>
#include <aws/sqs/model/ListQueuesResult.h>
#include <iostream>
//snippet-end:[sqs.cpp.list_queues.inc]

/**
 * List sqs queues within an aws account.
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[sqs.cpp.list_queues.code]
        Aws::SQS::SQSClient sqs;

        Aws::SQS::Model::ListQueuesRequest lq_req;

        auto lq_out = sqs.ListQueues(lq_req);
        if (lq_out.IsSuccess())
        {
            std::cout << "Queue Urls:" << std::endl << std::endl;
            const auto &queue_urls = lq_out.GetResult().GetQueueUrls();
            for (const auto &iter : queue_urls)
            {
                std::cout << " " << iter << std::endl;
            }
        }
        else
        {
            std::cout << "Error listing queues: " <<
                lq_out.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[sqs.cpp.list_queues.code]
    }
    Aws::ShutdownAPI(options);
}

