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

#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/ListMetricsRequest.h>
#include <aws/monitoring/model/ListMetricsResult.h>

#include <iostream>

static const char* SIMPLE_DATE_FORMAT_STR = "%Y-%m-%d";

/**
 * Lists cloud watch metrics according command line specified filter criteria
 */
int main(int argc, char** argv)
{
    if (argc > 3)
    {
        std::cout << "Usage: cw_list_metrics [metric_name] [metric_namespace]" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::CloudWatch::CloudWatchClient cw_client;

    Aws::CloudWatch::Model::ListMetricsRequest listMetricsRequest;
    if (argc > 1)
    {
        listMetricsRequest.SetMetricName(argv[1]);
    }

    if (argc > 2)
    {
        listMetricsRequest.SetNamespace(argv[2]);
    }

    bool done = false;
    bool header = false;
    while(!done)
    {
        auto listMetricsOutcome = cw_client.ListMetrics(listMetricsRequest);
        if(!listMetricsOutcome.IsSuccess())
        {
            std::cout << "Failed to list cloudwatch metrics:" << listMetricsOutcome.GetError().GetMessage() << std::endl;
            break;
        }

        if(!header)
        {
            std::cout << std::left << std::setw(48) << "MetricName" 
                                   << std::setw(32) << "Namespace" 
                                   << "DimensionNameValuePairs" << std::endl;
            header = true;
        }

        const auto& metrics = listMetricsOutcome.GetResult().GetMetrics();
        for (const auto& metric : metrics)
        {
            std::cout << std::left << std::setw(48) << metric.GetMetricName() 
                                   << std::setw(32) << metric.GetNamespace();
            const auto& dimensions = metric.GetDimensions();
            for (auto iter = dimensions.cbegin(); iter != dimensions.cend(); ++iter)
            {
                const auto& dimkv = *iter;
                std::cout << dimkv.GetName() << " = " << dimkv.GetValue();
                if(iter + 1 != dimensions.cend())
                {
                    std::cout << ", ";
                }
            }

            std::cout << std::endl;
        }

        const auto& nextToken = listMetricsOutcome.GetResult().GetNextToken();
        listMetricsRequest.SetNextToken(nextToken);
        done = nextToken.empty();
    }

    Aws::ShutdownAPI(options);

    return 0;
}



