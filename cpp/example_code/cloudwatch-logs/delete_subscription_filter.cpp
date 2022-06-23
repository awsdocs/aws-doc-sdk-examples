// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: delete_subscription_filter.cpp demonstrates how to delete an Amazon CloudWatch Logs subscription filter.
 *
 * Prerequisites:
 * A CloudWatch Logs subscription with a filter.
 *
 * Inputs:
 * - filter_name: The name of the filter.
 * - log_group: The name of the log group.
 *
 * Outputs:
 * The subscription filter is deleted.
 * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cwl.cpp.delete_subscription_filter.inc]

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/logs/CloudWatchLogsClient.h>
#include <aws/logs/model/DeleteSubscriptionFilterRequest.h>
#include <iostream>
//snippet-end:[cwl.cpp.delete_subscription_filter.inc]

/**
 * Delete a CloudWatch Logs subscription filter based on command-line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: delete_subscription_filter "
            << "<filter_name> <log_group_name>" << std::endl;
        return 1;
    }

    Aws::String filter_name(argv[1]);
    Aws::String log_group(argv[2]);
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
    //snippet-start:[cwl.cpp.delete_subscription_filter]

        Aws::CloudWatchLogs::CloudWatchLogsClient cwl;
        Aws::CloudWatchLogs::Model::DeleteSubscriptionFilterRequest request;
        request.SetFilterName(filter_name);
        request.SetLogGroupName(log_group);

        auto outcome = cwl.DeleteSubscriptionFilter(request);
        if (!outcome.IsSuccess()) {
            std::cout << "Failed to delete CloudWatch log subscription filter "
                << filter_name << ": " << outcome.GetError().GetMessage() <<
                std::endl;
        } else {
            std::cout << "Successfully deleted CloudWatch logs subscription " <<
                "filter " << filter_name << std::endl;
        }
    //snippet-end:[cwl.cpp.delete_subscription_filter]

    }
    Aws::ShutdownAPI(options);
    return 0;
}
