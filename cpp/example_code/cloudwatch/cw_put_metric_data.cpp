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
#include <aws/monitoring/model/PutMetricDataRequest.h>

#include <iostream>

/**
 * Attempts to put a sample metric data point with value based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: cw_put_metric_data <data_point_value>" << std::endl;
        return 1;
    }

    Aws::StringStream valueStream(argv[1]);
    double dataPointValue = 1.0;
    valueStream >> dataPointValue;

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::CloudWatch::CloudWatchClient cw_client;

    Aws::CloudWatch::Model::Dimension dimension;
    dimension.SetName("UNIQUE_PAGES");
    dimension.SetValue("URLS");

    Aws::CloudWatch::Model::MetricDatum datum;
    datum.SetMetricName("PAGES_VISITED");
    datum.SetUnit(Aws::CloudWatch::Model::StandardUnit::None);
    datum.SetValue(dataPointValue);
    datum.AddDimensions(dimension);

    Aws::CloudWatch::Model::PutMetricDataRequest putMetricDataRequest;
    putMetricDataRequest.SetNamespace("SITE/TRAFFIC");
    putMetricDataRequest.AddMetricData(datum);

    auto putMetricDataOutcome = cw_client.PutMetricData(putMetricDataRequest);
    if(!putMetricDataOutcome.IsSuccess())
    {
        std::cout << "Failed to put sample metric data:" << putMetricDataOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully put sample metric data" << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



