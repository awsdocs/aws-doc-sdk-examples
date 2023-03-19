/*
  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
  SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

//snippet-start:[ec2.cpp.allocate_address.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/AllocateAddressRequest.h>
#include <aws/ec2/model/AllocateAddressResponse.h>
#include <aws/ec2/model/AssociateAddressRequest.h>
#include <aws/ec2/model/AssociateAddressResponse.h>
#include <iostream>
//snippet-end:[ec2.cpp.allocate_address.inc]
#include "ec2_samples.h"

//! Allocate an Elastic IP address and associate it with an Amazon Elastic Compute Cloud
//! (Amazon EC2) instance.
/*!
  \sa AllocateAndAssociateAddress()
  \param instanceID: An EC2 instance ID.
  \param allocationId: String to return the allocation ID of the address.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::AllocateAndAssociateAddress(const Aws::String &instanceId,
                                              Aws::String &allocationId,
                                              const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.allocate_address.code]
    // snippet-start:[cpp.example_code.ec2.allocate_address.client]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);
    // snippet-end:[cpp.example_code.ec2.allocate_address.client]

    // snippet-start:[cpp.example_code.ec2.AllocateAddress]
    Aws::EC2::Model::AllocateAddressRequest request;
    request.SetDomain(Aws::EC2::Model::DomainType::vpc);

    const Aws::EC2::Model::AllocateAddressOutcome outcome =
            ec2Client.AllocateAddress(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to allocate Elastic IP address:" <<
                  outcome.GetError().GetMessage() << std::endl;
        return false;
    }

    allocationId = outcome.GetResult().GetAllocationId();
    // snippet-end:[cpp.example_code.ec2.AllocateAddress]

    // snippet-start:[cpp.example_code.ec2.AssociateAddress]
    Aws::EC2::Model::AssociateAddressRequest associate_request;
    associate_request.SetInstanceId(instanceId);
    associate_request.SetAllocationId(allocationId);

    const Aws::EC2::Model::AssociateAddressOutcome associate_outcome =
            ec2Client.AssociateAddress(associate_request);
    if (!associate_outcome.IsSuccess()) {
        std::cerr << "Failed to associate Elastic IP address " << allocationId
                  << " with instance " << instanceId << ":" <<
                  associate_outcome.GetError().GetMessage() << std::endl;
        return false;
    }

    std::cout << "Successfully associated Elastic IP address " << allocationId
              << " with instance " << instanceId << std::endl;
    // snippet-end:[cpp.example_code.ec2.AssociateAddress]
    // snippet-end:[ec2.cpp.allocate_address.code]

    return true;
}

/*
*  main function
*
*  Usage: 'run_allocate_address <instance_id>'
*
*  Prerequisites: An EC2 instance to allocate an address for.
*
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_allocate_address <instance_id>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::String instanceID = argv[1];
        Aws::String allocationID;
        AwsDoc::EC2::AllocateAndAssociateAddress(instanceID, allocationID,
                                                 clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
