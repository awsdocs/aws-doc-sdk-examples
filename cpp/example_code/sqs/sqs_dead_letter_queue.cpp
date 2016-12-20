/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/core/utils/json/JsonSerializer.h>
#include <aws/sqs/SQSClient.h>
#include <aws/sqs/model/SetQueueAttributesRequest.h>

#include <iostream>

Aws::String MakeRedrivePolicy(const Aws::String& deadLetterQueueArn, int maxMessages)
{
    Aws::Utils::Json::JsonValue redriveArnEntry;
    redriveArnEntry.AsString(deadLetterQueueArn);

    Aws::Utils::Json::JsonValue maxMessagesEntry;
    maxMessagesEntry.AsInteger(maxMessages);

    Aws::Utils::Json::JsonValue policyMap;
    policyMap.WithObject("deadLetterTargetArn", redriveArnEntry);
    policyMap.WithObject("maxReceiveCount", maxMessagesEntry);

    return policyMap.WriteReadable();
}

/**
 * Connects an sqs queue to an associated dead letter queue based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 4)
    {
        std::cout << "Usage: sqs_dead_letter_queue <source_queue_url> <dead_letter_queue_arn> <max_messages>" << std::endl;
        return 1;
    }

    Aws::String sourceQueueUrl = argv[1];
    Aws::String deadLetterQueueArn = argv[2];

    Aws::StringStream ss(argv[3]);
    int maxMessages = 1;
    ss >> maxMessages;

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::SQS::SQSClient sqs_client;

    Aws::String redrivePolicy = MakeRedrivePolicy(deadLetterQueueArn, maxMessages);

    Aws::SQS::Model::SetQueueAttributesRequest setQueueAttributesRequest;
    setQueueAttributesRequest.SetQueueUrl(sourceQueueUrl);
    setQueueAttributesRequest.AddAttributes(Aws::SQS::Model::QueueAttributeName::RedrivePolicy, redrivePolicy);

    auto setQueueAttributesOutcome = sqs_client.SetQueueAttributes(setQueueAttributesRequest);
    if(setQueueAttributesOutcome.IsSuccess())
    {
        std::cout << "Successfully set dead letter queue for queue  " << sourceQueueUrl  << " to " << deadLetterQueueArn << std::endl;
    }
    else
    {
        std::cout << "Error setting dead letter queue for queue " << sourceQueueUrl << ": " << setQueueAttributesOutcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



