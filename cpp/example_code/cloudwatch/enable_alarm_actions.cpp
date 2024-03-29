// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*

*/
/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: enable_alarm_actions.cpp demonstrates how to enable actions on an Amazon CloudWatch metric alarm.
 *
 * Prerequisites:
 * An Amazon CloudWatch metric alarm.
 *
 * Inputs:
 * - alarm_name: The name of the alarm (entered as the second argument in the command line).
 * - instanceId: The instance ID of the alarm (entered as the third argument in the command line).
 * - actionArn: The Amazon Resource Name (ARN) of the action (entered as the third argument in the command line).
 *
 * Outputs:
 * The metric alarm is enabled.
 * ///////////////////////////////////////////////////////////////////////// */
// snippet-start:[cw.cpp.enable_alarm_actions.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/EnableAlarmActionsRequest.h>
#include <aws/monitoring/model/PutMetricAlarmRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.enable_alarm_actions.inc]

void CreateAlarmAndEnableActions(
    const Aws::String& alarm_name, const Aws::String& instanceId,
    const Aws::String& actionArn)
{
    // snippet-start:[cw.cpp.enable_alarm_actions.code]
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
    request.AddAlarmActions(actionArn);

    Aws::CloudWatch::Model::Dimension dimension;
    dimension.SetName("InstanceId");
    dimension.SetValue(instanceId);
    request.AddDimensions(dimension);

    auto outcome = cw.PutMetricAlarm(request);
    if (!outcome.IsSuccess())
    {
        std::cout << "Failed to create CloudWatch alarm:" <<
            outcome.GetError().GetMessage() << std::endl;
        return;
    }

    Aws::CloudWatch::Model::EnableAlarmActionsRequest enable_request;
    enable_request.AddAlarmNames(alarm_name);

    auto enable_outcome = cw.EnableAlarmActions(enable_request);
    if (!enable_outcome.IsSuccess())
    {
        std::cout << "Failed to enable alarm actions:" <<
            enable_outcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully created alarm " << alarm_name <<
        " and enabled actions on it." << std::endl;
    // snippet-end:[cw.cpp.enable_alarm_actions.code]
}

/**
 * Adds a cloud watch alarm to an instance based on CPU utilization. This alarm
 * includes a command-line specified ARN that dictates what action to take when
 * the alarm transitions into the Alarm state
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage:" << std::endl << "  enable_alarm_actions" <<
            " <alarm_name> <instance_id> <alarm_action_arn>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alarm_name(argv[1]);
        Aws::String instanceId(argv[2]);
        Aws::String actionArn(argv[3]);

        CreateAlarmAndEnableActions(alarm_name, instanceId, actionArn);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

