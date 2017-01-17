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
#include <aws/sqs/model/ListQueuesRequest.h>
#include <aws/sqs/model/ListQueuesResult.h>
#include <iostream>

/**
 * List sqs queues within an aws account.
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::SQS::SQSClient sqs;

    Aws::SQS::Model::ListQueuesRequest lq_req;

    auto lq_out = sqs.ListQueues(lq_req);
    if(lq_out.IsSuccess()) {
        std::cout << "Queue Urls:" << std::endl << std::endl;
        const auto& queue_urls = lq_out.GetResult().GetQueueUrls();
        for(const auto& iter : queue_urls) {
            std::cout << " " << iter << std::endl;
        }
    } else {
        std::cout << "Error listing queues: " << lq_out.GetError().GetMessage()
            << std::endl;
    }

    Aws::ShutdownAPI(options);
}

