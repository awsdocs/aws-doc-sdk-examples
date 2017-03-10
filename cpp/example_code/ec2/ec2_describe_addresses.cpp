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
#include <aws/ec2/model/DescribeAddressesRequest.h>
#include <aws/ec2/model/DescribeAddressesResponse.h>
#include <iostream>

/**
 * Describes all elastic IP addresses
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::EC2::EC2Client ec2;
        Aws::EC2::Model::DescribeAddressesRequest request;
        auto outcome = ec2.DescribeAddresses(request);
        if (outcome.IsSuccess()) {
            std::cout << std::left << std::setw(20) << "InstanceId" <<
                std::setw(15) << "Public IP" << std::setw(10) << "Domain" <<
                std::setw(20) << "Allocation ID" << std::setw(25) << "NIC ID" <<
                std::endl;

            const auto &addresses = outcome.GetResult().GetAddresses();
            for (const auto &address : addresses)
            {
                Aws::String domainString =
                    Aws::EC2::Model::DomainTypeMapper::GetNameForDomainType(
                            address.GetDomain());

                std::cout << std::left << std::setw(20) <<
                    address.GetInstanceId() << std::setw(15) <<
                    address.GetPublicIp() << std::setw(10) << domainString <<
                    std::setw(20) << address.GetAllocationId() << std::setw(25)
                    << address.GetNetworkInterfaceId() << std::endl;
            }
        }
        else
        {
            std::cout << "Failed to describe elastic ip addresses:" <<
                outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

