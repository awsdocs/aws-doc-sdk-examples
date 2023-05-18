/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

//snippet-start:[sqs.cpp.list_queues.inc]
#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/ListQueuesRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.list_queues.inc]
#include "sqs_samples.h"

// snippet-start:[cpp.example_code.sqs.ListQueues]
//! List the Amazon Simple Queue Service (Amazon SQS) queues within an AWS account.
/*!
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool
AwsDoc::SQS::listQueues(const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[sqs.cpp.list_queues.code]
    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    Aws::SQS::Model::ListQueuesRequest lq_req;

    const Aws::SQS::Model::ListQueuesOutcome outcome = sqsClient.ListQueues(lq_req);
    if (outcome.IsSuccess()) {
        std::cout << "Queue Urls:" << std::endl << std::endl;
        const auto &queue_urls = outcome.GetResult().GetQueueUrls();
        for (const auto &iter: queue_urls) {
            std::cout << " " << iter << std::endl;
        }
    }
    else {
        std::cerr << "Error listing queues: " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    // snippet-end:[sqs.cpp.list_queues.code]

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.sqs.ListQueues]

/*
 *
 *  main function
 *
 *  Usage: 'run_list_queues'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[cpp.example_code.sqs.ListQueues.config]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.sqs.ListQueues.config]

        AwsDoc::SQS::listQueues(clientConfig);
    }
    Aws::ShutdownAPI(options);
}

#endif // TESTING_BUILD
