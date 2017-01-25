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
#include <aws/core/client/DefaultRetryStrategy.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/DeleteQueueRequest.h>
#include <iostream>

/**
 * Deletes an sqs queue based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2) {
        std::cout << "Usage: delete_queue <queue_url>" << std::endl;
        return 1;
    }

    Aws::String queue_url = argv[1];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        // disable retries so that bad urls don't hang the exe via retry loop
        Aws::Client::ClientConfiguration client_cfg;
        client_cfg.retryStrategy = Aws::MakeShared<Aws::Client::DefaultRetryStrategy>(
                "sqs_delete_queue", 0);
        Aws::SQS::SQSClient sqs(client_cfg);

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
    }

    Aws::ShutdownAPI(options);

    return 0;
}

