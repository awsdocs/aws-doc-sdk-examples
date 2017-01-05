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
#include <aws/ec2/model/AllocateAddressRequest.h>
#include <aws/ec2/model/AllocateAddressResponse.h>
#include <aws/ec2/model/AssociateAddressRequest.h>
#include <aws/ec2/model/AssociateAddressResponse.h>

#include <iostream>

void AllocateAndAssociateAddress(const Aws::String& instanceId)
{
    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::AllocateAddressRequest allocateAddressRequest;
    allocateAddressRequest.SetDomain(Aws::EC2::Model::DomainType::vpc);

    auto allocateAddressOutcome = ec2_client.AllocateAddress(allocateAddressRequest);
    if(!allocateAddressOutcome.IsSuccess())
    {
        std::cout << "Failed to allocate elastic ip address:" << allocateAddressOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    Aws::String allocationId = allocateAddressOutcome.GetResult().GetAllocationId();

    Aws::EC2::Model::AssociateAddressRequest associateAddressRequest;
    associateAddressRequest.SetInstanceId(instanceId);
    associateAddressRequest.SetAllocationId(allocationId);

    auto associateAddressOutcome = ec2_client.AssociateAddress(associateAddressRequest);
    if(!associateAddressOutcome.IsSuccess())
    {
        std::cout << "Failed to associate elastic ip address" << allocationId << " with instance " << instanceId << ":" << associateAddressOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully associated elastic ip address " << allocationId << " with instance " << instanceId << std::endl;
}

/**
 * Allocates an elastic IP address for an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: ec2_allocate_address <instance_id>" << std::endl;
        return 1;
    }

    Aws::String instanceId = argv[1];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    AllocateAndAssociateAddress(instanceId);

    Aws::ShutdownAPI(options);

    return 0;
}



