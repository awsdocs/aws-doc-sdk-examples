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

#include <aws/core/utils/Outcome.h>

#include <aws/logs/CloudWatchLogsClient.h>
#include <aws/logs/model/DescribeSubscriptionFiltersRequest.h>
#include <aws/logs/model/DescribeSubscriptionFiltersResult.h>

#include <iostream>
#include <iomanip>

/**
 * Lists cloudwatch subscription filters associated with a log group
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: cw_describe_subscription_filters <log_group_name>" << std::endl;
        return 1;
    }

    Aws::String logGroupName(argv[1]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::CloudWatchLogs::CloudWatchLogsClient cwl_client;

    Aws::CloudWatchLogs::Model::DescribeSubscriptionFiltersRequest describeSubscriptionFiltersRequest;
    describeSubscriptionFiltersRequest.SetLogGroupName(logGroupName);
    describeSubscriptionFiltersRequest.SetLimit(1);

    bool done = false;
    bool header = false;
    while(!done)
    {
        auto describeSubscriptionFiltersOutcome = cwl_client.DescribeSubscriptionFilters(describeSubscriptionFiltersRequest);
        if(!describeSubscriptionFiltersOutcome.IsSuccess())
        {
            std::cout << "Failed to describe cloudwatch subscription filters for log group " << logGroupName << ": " << describeSubscriptionFiltersOutcome.GetError().GetMessage() << std::endl;
            break;
        }

        if(!header)
        {
            std::cout << std::left << std::setw(32) << "Name" 
                                   << std::setw(64) << "FilterPattern"
                                   << std::setw(64) << "DestinationArn" << std::endl;
            header = true;
        }

        const auto& filters = describeSubscriptionFiltersOutcome.GetResult().GetSubscriptionFilters();
        for (const auto& filter : filters)
        {
            std::cout << std::left << std::setw(32) << filter.GetFilterName() 
                                   << std::setw(64) << filter.GetFilterPattern()
                                   << std::setw(64) << filter.GetDestinationArn() << std::endl;
        }

        const auto& nextToken = describeSubscriptionFiltersOutcome.GetResult().GetNextToken();
        describeSubscriptionFiltersRequest.SetNextToken(nextToken);
        done = nextToken.empty();
    }

    Aws::ShutdownAPI(options);

    return 0;
}



