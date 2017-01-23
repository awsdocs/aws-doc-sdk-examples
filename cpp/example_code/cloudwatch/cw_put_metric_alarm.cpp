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
#include <aws/monitoring/model/PutMetricAlarmRequest.h>

#include <iostream>

/**
 * Adds a cloud watch alarm to an instance based on CPU utilization
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: cw_put_metric_alarm <alarm_name> <instance_id>" << std::endl;
        return 1;
    }

    Aws::String alarmName(argv[1]);
    Aws::String instanceId(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::CloudWatch::CloudWatchClient cw_client;

        Aws::CloudWatch::Model::PutMetricAlarmRequest putMetricAlarmRequest;
        putMetricAlarmRequest.SetAlarmName(alarmName);
        putMetricAlarmRequest.SetComparisonOperator(Aws::CloudWatch::Model::ComparisonOperator::GreaterThanThreshold);
        putMetricAlarmRequest.SetEvaluationPeriods(1);
        putMetricAlarmRequest.SetMetricName("CPUUtilization");
        putMetricAlarmRequest.SetNamespace("AWS/EC2");
        putMetricAlarmRequest.SetPeriod(60);
        putMetricAlarmRequest.SetStatistic(Aws::CloudWatch::Model::Statistic::Average);
        putMetricAlarmRequest.SetThreshold(70.0);
        putMetricAlarmRequest.SetActionsEnabled(false);
        putMetricAlarmRequest.SetAlarmDescription("Alarm when server CPU exceeds 70%");
        putMetricAlarmRequest.SetUnit(Aws::CloudWatch::Model::StandardUnit::Seconds);

        Aws::CloudWatch::Model::Dimension dimension;
        dimension.SetName("InstanceId");
        dimension.SetValue(instanceId);

        putMetricAlarmRequest.AddDimensions(dimension);

        auto putMetricAlarmOutcome = cw_client.PutMetricAlarm(putMetricAlarmRequest);
        if (!putMetricAlarmOutcome.IsSuccess())
        {
            std::cout << "Failed to create cloudwatch alarm:" << putMetricAlarmOutcome.GetError().GetMessage() <<
            std::endl;
        }
        else
        {
            std::cout << "Successfully created cloudwatch alarm " << alarmName << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



