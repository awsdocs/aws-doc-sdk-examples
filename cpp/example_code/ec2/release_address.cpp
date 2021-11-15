// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
release_address.cpp demonstrates how to release an Amazon EC2 Elastic IP address.
*/

//snippet-start:[ec2.cpp.release_address.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/ReleaseAddressRequest.h>
#include <iostream>
//snippet-end:[ec2.cpp.release_address.inc]

/**
 * Releases an Elastic IP address based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: release_address <allocation_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String allocation_id = argv[1];

        // snippet-start:[ec2.cpp.release_address.code]
        Aws::Client::ClientConfiguration config;
        config.region = Aws::Region::US_WEST_2;

        Aws::EC2::EC2Client ec2(config);

        Aws::EC2::Model::ReleaseAddressRequest request;
        request.SetAllocationId(allocation_id);

        auto outcome = ec2.ReleaseAddress(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to release elastic ip address " <<
                allocation_id << ":" << outcome.GetError().GetMessage() <<
                std::endl;
        }
        else
        {
            std::cout << "Successfully released elastic ip address " <<
                allocation_id << std::endl;
        }
        // snippet-end:[ec2.cpp.release_address.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

