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

#include <aws/core/Aws.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/PurgeQueueRequest.h>
#include <iostream>
#include "sqs_samples.h"

// snippet-start:[cpp.example_code.sqs.PurgeQueue]
//! Delete the messages from an Amazon Simple Queue Service (Amazon SQS) queue.
/*!
  \param queueUrl: An Amazon SQS queue URL.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SQS::purgeQueue(const Aws::String &queueUrl,
                             const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    Aws::SQS::Model::PurgeQueueRequest request;
    request.SetQueueUrl(queueUrl);

    const Aws::SQS::Model::PurgeQueueOutcome outcome = sqsClient.PurgeQueue(request);
    if (outcome.IsSuccess()) {
        std::cout << "Successfully purged queue " << std::endl;
    }
    else {
        std::cout << "Error purging queue " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.sqs.PurgeQueue]

/*
*
*  main function
*
*  Usage: 'run_purge_queue <queue_url>'
*
*  Prerequisites: An existing Amazon SQS queue.
*
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_purge_queue <queue_url>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String queueUrl(argv[1]);


        Aws::Client::ClientConfiguration clientConfig;

        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::SQS::purgeQueue(queueUrl, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif

