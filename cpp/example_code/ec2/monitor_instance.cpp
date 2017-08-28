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
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/MonitorInstancesRequest.h>
#include <aws/ec2/model/MonitorInstancesResponse.h>
#include <aws/ec2/model/UnmonitorInstancesRequest.h>
#include <aws/ec2/model/UnmonitorInstancesResponse.h>
#include <iostream>

void EnableMonitoring(const Aws::String& instance_id)
{
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
}

void DisableMonitoring(const Aws::String& instance_id)
{
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
}

/**
 * Toggles detailed monitoring for an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: ec2_monitor_instance <instance_id> <true|false>" <<
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

