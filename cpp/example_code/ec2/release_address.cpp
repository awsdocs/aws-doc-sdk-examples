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
#include <aws/ec2/model/ReleaseAddressRequest.h>
#include <iostream>

/**
 * Releases an elastic ip address based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: ec2_release_address <allocation_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String allocation_id = argv[1];

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
    }
    Aws::ShutdownAPI(options);
    return 0;
}

