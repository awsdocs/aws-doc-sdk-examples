// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: delete_alarm.cpp demonstrates how to delete an Amazon CloudWatch alarm.
 *
 * Prerequisites:
 * - An Amazon CloudWatch metric alarm.
 *
 * Inputs:
 * - alarm_name (entered as second argument in command line).
 *
 * ///////////////////////////////////////////////////////////////////////// */

//snippet-start:[cw.cpp.delete_alarm.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/DeleteAlarmsRequest.h>
#include <iostream>
//snippet-end:[cw.cpp.delete_alarm.inc]

/**
 * Deletes a CloudWatch alarm
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: cw_delete_alarm <alarm_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alarm_name(argv[1]);

        // snippet-start:[cw.cpp.delete_alarm.code]
        Aws::CloudWatch::CloudWatchClient cw;
        Aws::CloudWatch::Model::DeleteAlarmsRequest request;
        request.AddAlarmNames(alarm_name);

        auto outcome = cw.DeleteAlarms(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to delete CloudWatch alarm:" <<
                outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted CloudWatch alarm " << alarm_name
                << std::endl;
        }
        // snippet-end:[cw.cpp.delete_alarm.code]

    }
    Aws::ShutdownAPI(options);
    return 0;
}



