 
//snippet-sourcedescription:[dead_letter_queue.cpp demonstrates how to enable the dead-letter functionality of an Amazon SQS queue.]
//snippet-keyword:[C++]
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
//snippet-start:[sqs.cpp.make_redrive_policy.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/json/JsonSerializer.h>
//snippet-end:[sqs.cpp.make_redrive_policy.inc]
//snippet-start:[sqs.cpp.set_redrive_policy.inc]
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>
#include <iostream>
//snippet-end:[sqs.cpp.set_redrive_policy.inc]

// snippet-start:[sqs.cpp.make_redrive_policy.code]
Aws::String MakeRedrivePolicy(const Aws::String& queue_arn, int max_msg)
{
    Aws::Utils::Json::JsonValue redrive_arn_entry;
    redrive_arn_entry.AsString(queue_arn);

    Aws::Utils::Json::JsonValue max_msg_entry;
    max_msg_entry.AsInteger(max_msg);

    Aws::Utils::Json::JsonValue policy_map;
    policy_map.WithObject("deadLetterTargetArn", redrive_arn_entry);
    policy_map.WithObject("maxReceiveCount", max_msg_entry);

    return policy_map.View().WriteReadable();
}
// snippet-end:[sqs.cpp.make_redrive_policy.code]

/**
 * Connects an sqs queue to an associated dead letter queue based on command
 * line input
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage: dead_letter_queue <source_queue_url> " <<
            "<dead_letter_queue_arn> <max_messages>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String src_queue_url = argv[1];
        Aws::String queue_arn = argv[2];

        Aws::StringStream ss(argv[3]);
        int max_msg = 1;
        ss >> max_msg;

        Aws::SQS::SQSClient sqs;

        Aws::String redrivePolicy = MakeRedrivePolicy(queue_arn, max_msg);

        // snippet-start:[sqs.cpp.set_redrive_policy.code]
        Aws::SQS::Model::SetQueueAttributesRequest request;
        request.SetQueueUrl(src_queue_url);
        request.AddAttributes(
            Aws::SQS::Model::QueueAttributeName::RedrivePolicy,
            redrivePolicy);

        auto outcome = sqs.SetQueueAttributes(request);
        if (outcome.IsSuccess())
        {
            std::cout << "Successfully set dead letter queue for queue  " <<
                src_queue_url << " to " << queue_arn << std::endl;
        }
        else
        {
            std::cout << "Error setting dead letter queue for queue " <<
                src_queue_url << ": " << outcome.GetError().GetMessage() <<
                std::endl;
        }
        // snippet-end:[sqs.cpp.set_redrive_policy.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

