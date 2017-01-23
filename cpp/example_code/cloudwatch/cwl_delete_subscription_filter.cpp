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
#include <aws/logs/model/DeleteSubscriptionFilterRequest.h>

#include <iostream>

/**
 * Deletes a cloud watch logs subscription filter, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: cw_delete_subscription_filter <filter_name> <log_group_name>" << std::endl;
        return 1;
    }

    Aws::String filterName(argv[1]);
    Aws::String logGroupName(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::CloudWatchLogs::CloudWatchLogsClient cwl_client;

        Aws::CloudWatchLogs::Model::DeleteSubscriptionFilterRequest deleteSubscriptionFilterRequest;
        deleteSubscriptionFilterRequest.SetFilterName(filterName);
        deleteSubscriptionFilterRequest.SetLogGroupName(logGroupName);

        auto deleteSubscriptionFilterOutcome = cwl_client.DeleteSubscriptionFilter(deleteSubscriptionFilterRequest);
        if (!deleteSubscriptionFilterOutcome.IsSuccess())
        {
            std::cout << "Failed to delete cloudwatch log subscription filter " << filterName << ": " <<
            deleteSubscriptionFilterOutcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted cloudwatch logs subscription filter " << filterName << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



