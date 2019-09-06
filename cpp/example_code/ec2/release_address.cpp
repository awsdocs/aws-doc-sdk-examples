 
//snippet-sourcedescription:[release_address.cpp demonstrates how to release an Amazon EC2 Elastic IP address.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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

