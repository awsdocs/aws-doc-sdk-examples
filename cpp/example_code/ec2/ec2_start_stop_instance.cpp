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
#include <aws/ec2/model/StartInstancesRequest.h>
#include <aws/ec2/model/StartInstancesResponse.h>
#include <aws/ec2/model/StopInstancesRequest.h>
#include <aws/ec2/model/StopInstancesResponse.h>

#include <iostream>

void StartInstance(const Aws::String& instanceId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::StartInstancesRequest startInstancesRequest;
    startInstancesRequest.AddInstanceIds(instanceId);
    startInstancesRequest.SetDryRun(true);

    auto startInstancesDryRunOutcome = ec2_client.StartInstances(startInstancesRequest);
    assert(!startInstancesDryRunOutcome.IsSuccess());
    if(startInstancesDryRunOutcome.GetError().GetErrorType() != Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to start instance " << instanceId << ": " << startInstancesDryRunOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    startInstancesRequest.SetDryRun(false);
    auto startInstancesOutcome = ec2_client.StartInstances(startInstancesRequest);
    if(!startInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to start instance " << instanceId << ": " << startInstancesOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully started instance " << instanceId << std::endl;
    }
}

void StopInstance(const Aws::String& instanceId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::StopInstancesRequest stopInstancesRequest;
    stopInstancesRequest.AddInstanceIds(instanceId);
    stopInstancesRequest.SetDryRun(true);

    auto stopInstancesDryRunOutcome = ec2_client.StopInstances(stopInstancesRequest);
    assert(!stopInstancesDryRunOutcome.IsSuccess());
    if(stopInstancesDryRunOutcome.GetError().GetErrorType() != Aws::EC2::EC2Errors::DRY_RUN_OPERATION)
    {
        std::cout << "Failed dry run to stop instance " << instanceId << ": " << stopInstancesDryRunOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    stopInstancesRequest.SetDryRun(false);
    auto stopInstancesOutcome = ec2_client.StopInstances(stopInstancesRequest);
    if(!stopInstancesOutcome.IsSuccess())
    {
        std::cout << "Failed to stop instance " << instanceId << ": " << stopInstancesOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully stopped instance " << instanceId << std::endl;
    }
}

void PrintUsage()
{
    std::cout << "Usage: ec2_start_stop_instance <instance_id> <start|stop>" << std::endl;
}

/**
 * Stops or starts an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        PrintUsage();
        return 1;
    }

    Aws::String instanceId = argv[1];

    bool startInstance;
    if(Aws::Utils::StringUtils::CaselessCompare(argv[2], "start"))
    {
        startInstance = true;
    }
    else if(Aws::Utils::StringUtils::CaselessCompare(argv[2], "stop"))
    {
        startInstance = false;
    }
    else
    {
        PrintUsage();
        return 1;
    }


    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if(startInstance)
    {
        StartInstance(instanceId);
    }
    else
    {
        StopInstance(instanceId);
    }

    Aws::ShutdownAPI(options);

    return 0;
}



