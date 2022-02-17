// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: describe_subscription_filters.cpp demonstrates how to list the subscription filters for an Amazon CloudWatch Logs resource.
 *
 * Prerequisites:
 * A CloudWatch Logs subscription with a filter.
 *
 * Inputs:
 * - log_group_name: The name of the log group.
 *
 * Outputs:
 * This subscription filters are described.
 * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cwl.cpp.describe_subscription_filters.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/logs/CloudWatchLogsClient.h>
#include <aws/logs/model/DescribeSubscriptionFiltersRequest.h>
#include <aws/logs/model/DescribeSubscriptionFiltersResult.h>
#include <iostream>
#include <iomanip>
//snippet-end:[cwl.cpp.describe_subscription_filters.inc]

/**
 * List CloudWatch subscription filters associated with a log group
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: describe_subscription_filters <log_group_name>"
            << std::endl;
        return 1;
    }

    Aws::String log_group(argv[1]);
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
    //snippet-start:[cwl.cpp.describe_subscription_filters]
        Aws::CloudWatchLogs::CloudWatchLogsClient cwl;
        Aws::CloudWatchLogs::Model::DescribeSubscriptionFiltersRequest request;
        request.SetLogGroupName(log_group);
        request.SetLimit(1);

        bool done = false;
        bool header = false;
        while (!done) {
            auto outcome = cwl.DescribeSubscriptionFilters(
                    request);
            if (!outcome.IsSuccess()) {
                std::cout << "Failed to describe CloudWatch subscription filters "
                    << "for log group " << log_group << ": " <<
                    outcome.GetError().GetMessage() << std::endl;
                break;
            }

            if (!header) {
                std::cout << std::left << std::setw(32) << "Name" <<
                    std::setw(64) << "FilterPattern" << std::setw(64) <<
                    "DestinationArn" << std::endl;
                header = true;
            }

            const auto &filters = outcome.GetResult().GetSubscriptionFilters();
            for (const auto &filter : filters) {
                std::cout << std::left << std::setw(32) <<
                    filter.GetFilterName() << std::setw(64) <<
                    filter.GetFilterPattern() << std::setw(64) <<
                    filter.GetDestinationArn() << std::endl;
            }

            const auto &next_token = outcome.GetResult().GetNextToken();
            request.SetNextToken(next_token);
            done = next_token.empty();
        }
        //snippet-end:[cwl.cpp.describe_subscription_filters]

    }
    Aws::ShutdownAPI(options);
    return 0;
}

