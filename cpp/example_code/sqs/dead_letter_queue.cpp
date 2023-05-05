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

//snippet-start:[sqs.cpp.make_redrive_policy.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/json/JsonSerializer.h>
//snippet-end:[sqs.cpp.make_redrive_policy.inc]
//snippet-start:[sqs.cpp.set_redrive_policy.inc]
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.set_redrive_policy.inc]
#include "sqs_samples.h"

static Aws::String MakeRedrivePolicy(const Aws::String &queueArn, int maxReceiveCount);

// snippet-start:[cpp.example_code.sqs.SetDeadLetterQueue]
//! Connect an Amazon Simple Queue Service (Amazon SQS) queue to an associated
//! dead-letter queue.
/*!
  \param srcQueueUrl: An Amazon SQS queue URL.
  \param deadLetterQueueARN: The Amazon Resource Name (ARN) of an Amazon SQS dead-letter queue.
  \param maxReceiveCount: The max receive count of a message before it is sent to the dead-letter queue.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SQS::setDeadLetterQueue(const Aws::String &srcQueueUrl,
                                     const Aws::String &deadLetterQueueARN,
                                     int maxReceiveCount,
                                     const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::String redrivePolicy = MakeRedrivePolicy(deadLetterQueueARN, maxReceiveCount);

    Aws::SQS::SQSClient sqsClient(clientConfiguration);

    // snippet-start:[sqs.cpp.set_redrive_policy.code]
    Aws::SQS::Model::SetQueueAttributesRequest request;
    request.SetQueueUrl(srcQueueUrl);
    request.AddAttributes(
            Aws::SQS::Model::QueueAttributeName::RedrivePolicy,
            redrivePolicy);

    const Aws::SQS::Model::SetQueueAttributesOutcome outcome =
            sqsClient.SetQueueAttributes(request);
    if (outcome.IsSuccess()) {
        std::cout << "Successfully set dead letter queue for queue  " <<
                  srcQueueUrl << " to " << deadLetterQueueARN << std::endl;
    }
    else {
        std::cerr << "Error setting dead letter queue for queue " <<
                  srcQueueUrl << ": " << outcome.GetError().GetMessage() <<
                  std::endl;
    }
    // snippet-end:[sqs.cpp.set_redrive_policy.code]

    return outcome.IsSuccess();
}

//! Make a redrive policy for a dead-letter queue.
/*!
  \param queueArn: An Amazon SQS ARN for the dead-letter queue.
  \param maxReceiveCount: The max receive count of a message before it is sent to the dead-letter queue.
  \return Aws::String: Policy as JSON string.
 */
// snippet-start:[sqs.cpp.make_redrive_policy.code]
Aws::String MakeRedrivePolicy(const Aws::String &queueArn, int maxReceiveCount) {
    Aws::Utils::Json::JsonValue redrive_arn_entry;
    redrive_arn_entry.AsString(queueArn);

    Aws::Utils::Json::JsonValue max_msg_entry;
    max_msg_entry.AsInteger(maxReceiveCount);

    Aws::Utils::Json::JsonValue policy_map;
    policy_map.WithObject("deadLetterTargetArn", redrive_arn_entry);
    policy_map.WithObject("maxReceiveCount", max_msg_entry);

    return policy_map.View().WriteReadable();
}
// snippet-end:[sqs.cpp.make_redrive_policy.code]
// snippet-end:[cpp.example_code.sqs.SetDeadLetterQueue]

/*
 *
 *  main function
 *
 *  Usage: 'run_dead_letter_queue <source_queue_url> <dead_letter_queue_arn> <max_messages>'
 *
 *  Prerequisites: An existing Amazon SQS queue and an existing dead-letter queue.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 4) {
        std::cout << "Usage: run_dead_letter_queue <source_queue_url> " <<
                  "<dead_letter_queue_arn> <max_messages>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String srcQueueUrl = argv[1];
        Aws::String deadLetterQueueArn = argv[2];

        Aws::StringStream ss(argv[3]);
        int maxReceiveCount = 1;
        ss >> maxReceiveCount;

        // snippet-start:[cpp.example_code.sqs.SetDeadLetterQueue.config]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        // snippet-end:[cpp.example_code.sqs.SetDeadLetterQueue.config]

        AwsDoc::SQS::setDeadLetterQueue(srcQueueUrl, deadLetterQueueArn,
                                        maxReceiveCount, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

