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

#include <aws/logs/CloudWatchLogsClient.h>
#include <aws/logs/model/PutSubscriptionFilterRequest.h>

#include <aws/core/utils/Outcome.h>

#include <iostream>

/**
 * Creates a cloud watch logs subscription filter, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 5)
    {
        std::cout << "Usage: cwe_put_subscription_filter <filter_name> <filter_pattern> <log_group_name> <lambda_function_arn>" << std::endl;
        return 1;
    }

    Aws::String filterName(argv[1]);
    Aws::String filterPattern(argv[2]);
    Aws::String logGroupName(argv[3]);
    Aws::String destinationArn(argv[4]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::CloudWatchLogs::CloudWatchLogsClient cwl_client;

    Aws::CloudWatchLogs::Model::PutSubscriptionFilterRequest putSubscriptionFilterRequest;
    putSubscriptionFilterRequest.SetFilterName(filterName);
    putSubscriptionFilterRequest.SetFilterPattern(filterPattern);
    putSubscriptionFilterRequest.SetLogGroupName(logGroupName);
    putSubscriptionFilterRequest.SetDestinationArn(destinationArn);

    auto putSubscriptionFilterOutcome = cwl_client.PutSubscriptionFilter(putSubscriptionFilterRequest);
    if(!putSubscriptionFilterOutcome.IsSuccess())
    {
        std::cout << "Failed to create cloudwatch logs subscription filter " << filterName << ": " << putSubscriptionFilterOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully created cloudwatch logs subscription filter " << filterName << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



