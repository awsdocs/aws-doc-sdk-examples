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

void EnableMonitoring(const Aws::String& instanceId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::MonitorInstancesRequest monitorInstancesRequest;
    monitorInstancesRequest.AddInstanceIds(instanceId);
    monitorInstancesRequest.SetDryRun(true);

    auto monitorInstancesDryRunOutcome = ec2_client.MonitorInstances(monitorInstancesRequest);
    assert(!monitorInstancesDryRunOutcome.IsSuccess());
    if(monitorInstancesDryRunOutcome.GetError().GetErrorType() != Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to enable monitoring on instance " << instanceId << ": " << monitorInstancesDryRunOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    monitorInstancesRequest.SetDryRun(false);
    auto monitorInstancesOutcome = ec2_client.MonitorInstances(monitorInstancesRequest);
    if(!monitorInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to enable monitoring on instance " << instanceId << ": " << monitorInstancesOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully enabled monitoring on instance " << instanceId << std::endl;
    }
}

void DisableMonitoring(const Aws::String& instanceId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::UnmonitorInstancesRequest unmonitorInstancesRequest;
    unmonitorInstancesRequest.AddInstanceIds(instanceId);
    unmonitorInstancesRequest.SetDryRun(true);

    auto unmonitorInstancesDryRunOutcome = ec2_client.UnmonitorInstances(unmonitorInstancesRequest);
    assert(!unmonitorInstancesDryRunOutcome.IsSuccess());
    if(unmonitorInstancesDryRunOutcome.GetError().GetErrorType() != Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to disable monitoring on instance " << instanceId << ": " << unmonitorInstancesDryRunOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    unmonitorInstancesRequest.SetDryRun(false);
    auto unmonitorInstancesOutcome = ec2_client.UnmonitorInstances(unmonitorInstancesRequest);
    if(!unmonitorInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to disable monitoring on instance " << instanceId << ": " << unmonitorInstancesOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully disable monitoring on instance " << instanceId << std::endl;
    }
}

/**
 * Toggles detailed monitoring for an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: ec2_monitor_instance <instance_id> <true|false>" << std::endl;
        return 1;
    }

    Aws::String instanceId = argv[1];

    bool enableMonitoring = false;
    Aws::StringStream ss(argv[2]);
    ss >> std::boolalpha >> enableMonitoring;

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if(enableMonitoring)
    {
        EnableMonitoring(instanceId);
    }
    else
    {
        DisableMonitoring(instanceId);
    }

    Aws::ShutdownAPI(options);

    return 0;
}



