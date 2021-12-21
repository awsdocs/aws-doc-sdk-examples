// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*

*/
/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: disable_alarm_actions.cpp demonstrates how to disable actions on an Amazon CloudWatch alarm.
 *
 * Prerequisites:
 * An Amazon CloudWatch metric alarm with at least one action.
 *
 * Inputs:
 * - alarm_name (entered as second argument in command line)
 *
 * Outputs:
 * The alarm is disabled.
 * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cw.cpp.disable_alarm_actions.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/DisableAlarmActionsRequest.h>
#include <iostream>
//snippet-end:[cw.cpp.disable_alarm_actions.inc]

/**
 * Disable actions on a CloudWatch alarm based on command-line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: disable_alarm_actions <alarm_name>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::String alarm_name(argv[1]);

        //snippet-start:[cw.cpp.disable_alarm_actions.code]
        Aws::CloudWatch::CloudWatchClient cw;

        Aws::CloudWatch::Model::DisableAlarmActionsRequest disableAlarmActionsRequest;
        disableAlarmActionsRequest.AddAlarmNames(alarm_name);

        auto disableAlarmActionsOutcome = cw.DisableAlarmActions(disableAlarmActionsRequest);
        if (!disableAlarmActionsOutcome.IsSuccess())
        {
            std::cout << "Failed to disable actions for alarm " << alarm_name <<
                ": " << disableAlarmActionsOutcome.GetError().GetMessage() <<
                std::endl;
        }
        else
        {
            std::cout << "Successfully disabled actions for alarm " <<
                alarm_name << std::endl;
        }
        //snippet-end:[cw.cpp.disable_alarm_actions.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

