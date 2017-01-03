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
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesResponse.h>
#include <aws/ec2/model/DescribeRegionsRequest.h>
#include <aws/ec2/model/DescribeRegionsResponse.h>

#include <iostream>

/**
 * Describes all regions and zones
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::EC2::EC2Client ec2_client;

    Aws::EC2::Model::DescribeRegionsRequest describeRegionsRequest;

    auto describeRegionsOutcome = ec2_client.DescribeRegions(describeRegionsRequest);
    if(describeRegionsOutcome.IsSuccess())
    {
        std::cout << std::left << std::setw(32) << "RegionName" 
                               << std::setw(64) << "Endpoint" << std::endl;

        const auto& regions = describeRegionsOutcome.GetResult().GetRegions();
        for(const auto& region : regions)
        {
            std::cout << std::left << std::setw(32) << region.GetRegionName() 
                                   << std::setw(64) << region.GetEndpoint() << std::endl;
        }
    }
    else
    {
        std::cout << "Failed to describe regions:" << describeRegionsOutcome.GetError().GetMessage() << std::endl;
    }

    std::cout << std::endl;

    Aws::EC2::Model::DescribeAvailabilityZonesRequest describeAvailabilityZonesRequest;

    auto describeAvailabilityZonesOutcome = ec2_client.DescribeAvailabilityZones(describeAvailabilityZonesRequest);
    if(describeAvailabilityZonesOutcome.IsSuccess())
    {
        std::cout << std::left << std::setw(32) << "ZoneName"
                               << std::setw(20) << "State"
                               << std::setw(32) << "Region" << std::endl;

        const auto& availabilityZones = describeAvailabilityZonesOutcome.GetResult().GetAvailabilityZones();
        for(const auto& availabilityZone : availabilityZones)
        {
            Aws::String stateString = Aws::EC2::Model::AvailabilityZoneStateMapper::GetNameForAvailabilityZoneState(availabilityZone.GetState());
            std::cout << std::left << std::setw(32) << availabilityZone.GetZoneName()
                                   << std::setw(20) << stateString 
                                   << std::setw(32) << availabilityZone.GetRegionName() << std::endl;
        }
    }
    else
    {
        std::cout << "Failed to describe availability zones:" << describeAvailabilityZonesOutcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



