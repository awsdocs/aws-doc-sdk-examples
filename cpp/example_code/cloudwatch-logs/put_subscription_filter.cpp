// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_subscription_filter.cpp demonstrates how to create an Amazon Cloudwatch Logs subscription filter.
 *
 * Inputs:
 * - filter_name: The name of the filter.
 * - filter_pattern: The filter pattern.
 * - log_group_name: The name of the log group.
 * - lambda_function_arn: The Amazon Resource Name (ARN) of the AWS Lambda function.
 *
 * Outputs:
 * A subscription filter is created.
   * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cwl.cpp.put_subscription_filter.inc]
#include <aws/core/Aws.h>
#include <aws/logs/CloudWatchLogsClient.h>
#include <aws/logs/model/PutSubscriptionFilterRequest.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>
//snippet-end:[cwl.cpp.put_subscription_filter.inc]
/**
 * Create a CloudWatch Logs subscription filter based on command-line input
 */
int main(int argc, char** argv)
{
    if (argc != 5)
    {
        std::cout << "Usage: " << std::endl << "  put_subscription_filter "
            << "<filter_name> <filter_pattern> <log_group_name> " <<
            "<lambda_function_arn>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String filter_name(argv[1]);
        Aws::String filter_pattern(argv[2]);
        Aws::String log_group(argv[3]);
        Aws::String dest_arn(argv[4]);

        //snippet-start:[cwl.cpp.put_subscription_filter]
        Aws::CloudWatchLogs::CloudWatchLogsClient cwl;
        Aws::CloudWatchLogs::Model::PutSubscriptionFilterRequest request;
        request.SetFilterName(filter_name);
        request.SetFilterPattern(filter_pattern);
        request.SetLogGroupName(log_group);
        request.SetDestinationArn(dest_arn);
        auto outcome = cwl.PutSubscriptionFilter(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to create CloudWatch logs subscription filter "
                << filter_name << ": " << outcome.GetError().GetMessage() <<
                std::endl;
        }
        else
        {
            std::cout << "Successfully created CloudWatch logs subscription " <<
                "filter " << filter_name << std::endl;
        }
        //snippet-end:[cwl.cpp.put_subscription_filter]

    }
    Aws::ShutdownAPI(options);
    return 0;
}

