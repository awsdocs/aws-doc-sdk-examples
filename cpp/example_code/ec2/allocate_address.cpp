// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
allocate_address.cpp demonstrates how to allocate an Elastic IP address for an Amazon EC2 instance.

*/

//snippet-start:[ec2.cpp.allocate_address.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/AllocateAddressRequest.h>
#include <aws/ec2/model/AllocateAddressResponse.h>
#include <aws/ec2/model/AssociateAddressRequest.h>
#include <aws/ec2/model/AssociateAddressResponse.h>
#include <iostream>
//snippet-end:[ec2.cpp.allocate_address.inc]

void AllocateAndAssociateAddress(const Aws::String& instance_id)
{
    // snippet-start:[ec2.cpp.allocate_address.code]
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
    // snippet-end:[ec2.cpp.allocate_address.code]
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

