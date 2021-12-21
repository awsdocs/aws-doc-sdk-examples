// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_metric_data.cpp demonstrates how to submit Amazon CloudWatch metric data.
 *
 * Prerequisites:
 * An Amazon CloudWatch metric.
 *
 * Inputs:
 * - value_stream: The CloudWatch metric stream (entered as the second argument in the command line).
 *
 * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cw.cpp.put_metric_data.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/PutMetricDataRequest.h>
#include <iostream>
//snippet-end:[cw.cpp.put_metric_data.inc]

/**
 * Attempts to put a sample metric data point with value based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: put_metric_data <data_point_value>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::StringStream value_stream(argv[1]);
        double data_point = 1.0;
        value_stream >> data_point;

        // snippet-start:[cw.cpp.put_metric_data.code]
        Aws::CloudWatch::CloudWatchClient cw;

        Aws::CloudWatch::Model::Dimension dimension;
        dimension.SetName("UNIQUE_PAGES");
        dimension.SetValue("URLS");

        Aws::CloudWatch::Model::MetricDatum datum;
        datum.SetMetricName("PAGES_VISITED");
        datum.SetUnit(Aws::CloudWatch::Model::StandardUnit::None);
        datum.SetValue(data_point);
        datum.AddDimensions(dimension);

        Aws::CloudWatch::Model::PutMetricDataRequest request;
        request.SetNamespace("SITE/TRAFFIC");
        request.AddMetricData(datum);

        auto outcome = cw.PutMetricData(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to put sample metric data:" <<
                outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully put sample metric data" << std::endl;
        }
        // snippet-end:[cw.cpp.put_metric_data.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

