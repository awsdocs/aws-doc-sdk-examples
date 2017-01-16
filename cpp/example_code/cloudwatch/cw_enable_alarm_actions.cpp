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
#include <aws/monitoring/model/EnableAlarmActionsRequest.h>
#include <aws/monitoring/model/PutMetricAlarmRequest.h>

#include <iostream>

void CreateAlarmAndEnableActions(const Aws::String& alarmName, const Aws::String& instanceId, const Aws::String& actionArn)
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
    putMetricAlarmRequest.AddAlarmActions(actionArn);

    Aws::CloudWatch::Model::Dimension dimension;
    dimension.SetName("InstanceId");
    dimension.SetValue(instanceId);

    putMetricAlarmRequest.AddDimensions(dimension);

    auto putMetricAlarmOutcome = cw_client.PutMetricAlarm(putMetricAlarmRequest);
    if(!putMetricAlarmOutcome.IsSuccess())
    {
        std::cout << "Failed to create cloudwatch alarm:" << putMetricAlarmOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    Aws::CloudWatch::Model::EnableAlarmActionsRequest enableAlarmActionsRequest;
    enableAlarmActionsRequest.AddAlarmNames(alarmName);

    auto enableAlarmActionsOutcome = cw_client.EnableAlarmActions(enableAlarmActionsRequest);
    if (!enableAlarmActionsOutcome.IsSuccess())
    {
        std::cout << "Failed to enable alarm actions:" << enableAlarmActionsOutcome.GetError().GetMessage() << std::endl;
        return;
    }
    
    std::cout << "Successfully created alarm " << alarmName << " and enabled actions on it." << std::endl;
}

/**
 * Adds a cloud watch alarm to an instance based on CPU utilization.  This alarm includes a command-line specified
 * ARN that dictates what action to take when the alarm transitions into the Alarm state
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage: cw_enable_alarm_actions <alarm_name> <instance_id> <alarm_action_arn>" << std::endl;
        return 1;
    }

    Aws::String alarmName(argv[1]);
    Aws::String instanceId(argv[2]);
    Aws::String actionArn(argv[3]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    CreateAlarmAndEnableActions(alarmName, instanceId, actionArn);

    Aws::ShutdownAPI(options);

    return 0;
}



