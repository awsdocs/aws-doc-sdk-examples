// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
monitor_instance.cpp demonstrates how to toggle detailed monitoring of an Amazon EC2 instance.

*/


//snippet-start:[ec2.cpp.monitor_instance.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/MonitorInstancesRequest.h>
#include <aws/ec2/model/MonitorInstancesResponse.h>
#include <aws/ec2/model/UnmonitorInstancesRequest.h>
#include <aws/ec2/model/UnmonitorInstancesResponse.h>
#include <iostream>
//snippet-end:[ec2.cpp.monitor_instance.inc]

void EnableMonitoring(const Aws::String& instance_id)
{
    // snippet-start:[ec2.cpp.enable_monitor_instance.code]
    Aws::EC2::EC2Client ec2;
    Aws::EC2::Model::MonitorInstancesRequest request;
    request.AddInstanceIds(instance_id);
    request.SetDryRun(true);

    auto dry_run_outcome = ec2.MonitorInstances(request);
    assert(!dry_run_outcome.IsSuccess());
    if (dry_run_outcome.GetError().GetErrorType()
        != Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to enable monitoring on instance " <<
            instance_id << ": " << dry_run_outcome.GetError().GetMessage() <<
            std::endl;
        return;
    }

    request.SetDryRun(false);
    auto monitorInstancesOutcome = ec2.MonitorInstances(request);
    if (!monitorInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to enable monitoring on instance " <<
            instance_id << ": " <<
            monitorInstancesOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully enabled monitoring on instance " <<
            instance_id << std::endl;
    }
    // snippet-end:[ec2.cpp.enable_monitor_instance.code]
}

void DisableMonitoring(const Aws::String& instance_id)
{
    // snippet-start:[ec2.cpp.disable_monitor_instance.code]
    Aws::EC2::EC2Client ec2;
    Aws::EC2::Model::UnmonitorInstancesRequest unrequest;
    unrequest.AddInstanceIds(instance_id);
    unrequest.SetDryRun(true);

    auto undry_run_outcome = ec2.UnmonitorInstances(unrequest);
    assert(!undry_run_outcome.IsSuccess());
    if (undry_run_outcome.GetError().GetErrorType() !=
        Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to disable monitoring on instance " <<
            instance_id << ": " << undry_run_outcome.GetError().GetMessage() <<
            std::endl;
        return;
    }

    unrequest.SetDryRun(false);
    auto unmonitorInstancesOutcome = ec2.UnmonitorInstances(unrequest);
    if (!unmonitorInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to disable monitoring on instance " << instance_id
            << ": " << unmonitorInstancesOutcome.GetError().GetMessage() <<
            std::endl;
    }
    else
    {
        std::cout << "Successfully disable monitoring on instance " <<
            instance_id << std::endl;
    }
    // snippet-end:[ec2.cpp.disable_monitor_instance.code]
}

/**
 * Toggles detailed monitoring for an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: monitor_instance <instance_id> <true|false>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String instance_id = argv[1];

        bool enableMonitoring = false;
        Aws::StringStream ss(argv[2]);
        ss >> std::boolalpha >> enableMonitoring;

        if (enableMonitoring)
        {
            EnableMonitoring(instance_id);
        }
        else
        {
            DisableMonitoring(instance_id);
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

