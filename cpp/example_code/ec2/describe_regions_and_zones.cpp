 
//snippet-sourcedescription:[describe_regions_and_zones.cpp demonstrates how to retrieve information about the regions and availability zones of an Amazon EC2 instance.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/ec2/model/DescribeRegionsRequest.h>
#include <aws/ec2/model/DescribeRegionsResponse.h>
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesResponse.h>
#include <iomanip>
#include <iostream>
#include <iomanip>

/**
 * Describes all regions and zones
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::EC2::EC2Client ec2;
        Aws::EC2::Model::DescribeRegionsRequest request;
        auto outcome = ec2.DescribeRegions(request);
        if (outcome.IsSuccess())
        {
            std::cout << std::left <<
                std::setw(32) << "RegionName" <<
                std::setw(64) << "Endpoint" << std::endl;

            const auto &regions = outcome.GetResult().GetRegions();
            for (const auto &region : regions)
            {
                std::cout << std::left <<
                    std::setw(32) << region.GetRegionName() <<
                    std::setw(64) << region.GetEndpoint() << std::endl;
            }
        }
        else
        {
            std::cout << "Failed to describe regions:" <<
                outcome.GetError().GetMessage() << std::endl;
        }

        std::cout << std::endl;
        Aws::EC2::Model::DescribeAvailabilityZonesRequest describe_request;
        auto describe_outcome = ec2.DescribeAvailabilityZones(describe_request);

        if (describe_outcome.IsSuccess())
        {
            std::cout << std::left <<
                std::setw(32) << "ZoneName" <<
                std::setw(20) << "State" <<
                std::setw(32) << "Region" << std::endl;

            const auto &zones =
                describe_outcome.GetResult().GetAvailabilityZones();

            for (const auto &zone : zones)
            {
                Aws::String stateString =
                    Aws::EC2::Model::AvailabilityZoneStateMapper::GetNameForAvailabilityZoneState(
                        zone.GetState());
                std::cout << std::left <<
                    std::setw(32) << zone.GetZoneName() <<
                    std::setw(20) << stateString <<
                    std::setw(32) << zone.GetRegionName() << std::endl;
            }
        }
        else
        {
            std::cout << "Failed to describe availability zones:" <<
                describe_outcome.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

