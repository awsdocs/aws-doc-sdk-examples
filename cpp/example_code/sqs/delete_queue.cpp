
/*
Purpose:
delete_queue.cpp demonstrates how to delete an Amazon SQS queue.





// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
//snippet-start:[sqs.cpp.delete_queue.inc]
#include <aws/core/Aws.h>
#include <aws/core/client/DefaultRetryStrategy.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/DeleteQueueRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.delete_queue.inc]

/**
 * Deletes an sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_queue <queue_url>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queue_url = argv[1];

        // disable retries so that bad urls don't hang the exe via retry loop
        Aws::Client::ClientConfiguration client_cfg;
        client_cfg.retryStrategy =
            Aws::MakeShared<Aws::Client::DefaultRetryStrategy>(
                "sqs_delete_queue", 0);
        Aws::SQS::SQSClient sqs(client_cfg);

        // snippet-start:[sqs.cpp.delete_queue.code]
        Aws::SQS::Model::DeleteQueueRequest dq_req;
        dq_req.SetQueueUrl(queue_url);

        auto dq_out = sqs.DeleteQueue(dq_req);
        if (dq_out.IsSuccess())
        {
            std::cout << "Successfully deleted queue with url " << queue_url <<
                std::endl;
        }
        else
        {
            std::cout << "Error deleting queue " << queue_url << ": " <<
                dq_out.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[sqs.cpp.delete_queue.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

