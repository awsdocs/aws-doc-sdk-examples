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

void AllocateAndAssociateAddress(const Aws::String& instance_id)
{
    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::AllocateAddressRequest request;
    request.SetDomain(Aws::EC2::Model::DomainType::vpc);

    auto outcome = ec2.AllocateAddress(request);
    if (!outcome.IsSuccess())
    {
        std::cout << "Failed to allocate elastic ip address:" <<
            outcome.GetError().GetMessage() << std::endl;
        return;
    }

    Aws::String allocation_id = outcome.GetResult().GetAllocationId();

    Aws::EC2::Model::AssociateAddressRequest associate_request;
    associate_request.SetInstanceId(instance_id);
    associate_request.SetAllocationId(allocation_id);

    auto associate_outcome = ec2.AssociateAddress(associate_request);
    if (!associate_outcome.IsSuccess())
    {
        std::cout << "Failed to associate elastic ip address" << allocation_id
            << " with instance " << instance_id << ":" <<
            associate_outcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully associated elastic ip address " << allocation_id
        << " with instance " << instance_id << std::endl;
}

/**
 * Allocates an elastic IP address for an ec2 instance based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: allocate_address <instance_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String instance_id = argv[1];

        AllocateAndAssociateAddress(instance_id);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

