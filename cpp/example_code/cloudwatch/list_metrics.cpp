// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: list_metrics.cpp demonstrates how to retrieve and filter Amazon CloudWatch metrics.
 *
 * Prerequisites:
 * An Amazon CloudWatch metric.
 *
 * Inputs:
 * - metric_name: The name of the alarm metric (entered as the second argument in the command line).
 * - metric_namespace: The name of the namespace (entered as the third argument in the command line).
 *
 * Outputs:
 * A list of metrics.
 * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cw.cpp.list_metrics.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/ListMetricsRequest.h>
#include <aws/monitoring/model/ListMetricsResult.h>
#include <iomanip>
#include <iostream>
//snippet-end:[cw.cpp.list_metrics.inc]

static const char* SIMPLE_DATE_FORMAT_STR = "%Y-%m-%d";

/**
 * Lists cloud watch metrics according command line specified filter criteria
 */
int main(int argc, char** argv)
{
    if (argc > 3)
    {
        std::cout << "Usage: list_metrics [metric_name] [metric_namespace]"
            << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[cw.cpp.list_metrics.code]
        Aws::CloudWatch::CloudWatchClient cw;
        Aws::CloudWatch::Model::ListMetricsRequest request;

        if (argc > 1)
        {
            request.SetMetricName(argv[1]);
        }

        if (argc > 2)
        {
            request.SetNamespace(argv[2]);
        }

        bool done = false;
        bool header = false;
        while (!done)
        {
            auto outcome = cw.ListMetrics(request);
            if (!outcome.IsSuccess())
            {
                std::cout << "Failed to list CloudWatch metrics:" <<
                    outcome.GetError().GetMessage() << std::endl;
                break;
            }

            if (!header)
            {
                std::cout << std::left << std::setw(48) << "MetricName" <<
                    std::setw(32) << "Namespace" << "DimensionNameValuePairs" <<
                    std::endl;
                header = true;
            }

            const auto &metrics = outcome.GetResult().GetMetrics();
            for (const auto &metric : metrics)
            {
                std::cout << std::left << std::setw(48) <<
                    metric.GetMetricName() << std::setw(32) <<
                    metric.GetNamespace();
                const auto &dimensions = metric.GetDimensions();
                for (auto iter = dimensions.cbegin();
                    iter != dimensions.cend(); ++iter)
                {
                    const auto &dimkv = *iter;
                    std::cout << dimkv.GetName() << " = " << dimkv.GetValue();
                    if (iter + 1 != dimensions.cend())
                    {
                        std::cout << ", ";
                    }
                }
                std::cout << std::endl;
            }

            const auto &next_token = outcome.GetResult().GetNextToken();
            request.SetNextToken(next_token);
            done = next_token.empty();
        }
        // snippet-end:[cw.cpp.list_metrics.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

