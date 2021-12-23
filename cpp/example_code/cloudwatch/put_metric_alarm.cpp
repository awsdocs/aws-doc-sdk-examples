// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_metric_alarm.cpp demonstrates how to attach an Amazon CloudWatch metrics alarm.
 *
 * Prerequisites:
 * An Amazon CloudWatch metric with an alarm.
 *
 * Inputs:
 * - put_metric_alarm: The name of the metric (entered as the first argument in the command line).
 * - alarm_name: The name of the alarm (entered as the second argument in the command line).
 * - instance_id: The instance id of the alarm (entered as the third argument in the command line).
 *
 * Output:
 * The alarm is added to the metric.
 * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cw.cpp.put_metric_alarm.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/PutMetricAlarmRequest.h>
#include <iostream>
//snippet-end:[cw.cpp.put_metric_alarm.inc]

/**
 * Adds a CloudWatch alarm to an instance based on CPU utilization.
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage:" << "  put_metric_alarm " <<
            "<alarm_name> <instance_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alarm_name(argv[1]);
        Aws::String instanceId(argv[2]);

        // snippet-start:[cw.cpp.put_metric_alarm.code]
        Aws::CloudWatch::CloudWatchClient cw;
        Aws::CloudWatch::Model::PutMetricAlarmRequest request;
        request.SetAlarmName(alarm_name);
        request.SetComparisonOperator(
            Aws::CloudWatch::Model::ComparisonOperator::GreaterThanThreshold);
        request.SetEvaluationPeriods(1);
        request.SetMetricName("CPUUtilization");
        request.SetNamespace("AWS/EC2");
        request.SetPeriod(60);
        request.SetStatistic(Aws::CloudWatch::Model::Statistic::Average);
        request.SetThreshold(70.0);
        request.SetActionsEnabled(false);
        request.SetAlarmDescription("Alarm when server CPU exceeds 70%");
        request.SetUnit(Aws::CloudWatch::Model::StandardUnit::Seconds);

        Aws::CloudWatch::Model::Dimension dimension;
        dimension.SetName("InstanceId");
        dimension.SetValue(instanceId);

        request.AddDimensions(dimension);

        auto outcome = cw.PutMetricAlarm(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to create CloudWatch alarm:" <<
                outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully created CloudWatch alarm " << alarm_name
                << std::endl;
        }
        // snippet-end:[cw.cpp.put_metric_alarm.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

