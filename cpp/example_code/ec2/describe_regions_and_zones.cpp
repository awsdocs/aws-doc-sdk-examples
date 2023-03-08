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

//snippet-start:[ec2.cpp.describe_regions.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DescribeRegionsRequest.h>
#include <aws/ec2/model/DescribeRegionsResponse.h>
//snippet-end:[ec2.cpp.describe_regions.inc]
//snippet-start:[ec2.cpp.describe_zones.inc]
#include <aws/ec2/model/DescribeAvailabilityZonesRequest.h>
#include <aws/ec2/model/DescribeAvailabilityZonesResponse.h>
//snippet-end:[ec2.cpp.describe_zones.inc]
#include <iomanip>
#include <iostream>
#include "ec2_samples.h"

//! Describe all Amazon Elastic Compute Cloud (Amazon EC2) Regions and Availability Zones.
/*!
  \sa DescribeRegionsAndZones()
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::DescribeRegionsAndZones(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.describe_regions.code]
    // snippet-start:[cpp.example_code.ec2.describe_regions.client]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);
    // snippet-end:[cpp.example_code.ec2.describe_regions.client]
    // snippet-start:[cpp.example_code.ec2.DescribeRegions]
    Aws::EC2::Model::DescribeRegionsRequest request;
    auto outcome = ec2Client.DescribeRegions(request);
    bool result = true;
    if (outcome.IsSuccess()) {
        std::cout << std::left <<
                  std::setw(32) << "RegionName" <<
                  std::setw(64) << "Endpoint" << std::endl;

        const auto &regions = outcome.GetResult().GetRegions();
        for (const auto &region: regions) {
            std::cout << std::left <<
                      std::setw(32) << region.GetRegionName() <<
                      std::setw(64) << region.GetEndpoint() << std::endl;
        }
    }
    else {
        std::cerr << "Failed to describe regions:" <<
                  outcome.GetError().GetMessage() << std::endl;
        result = false;
    }
    // snippet-end:[cpp.example_code.ec2.DescribeRegions]
    // snippet-end:[ec2.cpp.describe_regions.code]

    std::cout << std::endl;

    // snippet-start:[ec2.cpp.describe_zones.code]
    Aws::EC2::Model::DescribeAvailabilityZonesRequest describe_request;
    auto describe_outcome = ec2Client.DescribeAvailabilityZones(describe_request);

    if (describe_outcome.IsSuccess()) {
        std::cout << std::left <<
                  std::setw(32) << "ZoneName" <<
                  std::setw(20) << "State" <<
                  std::setw(32) << "Region" << std::endl;

        const auto &zones =
                describe_outcome.GetResult().GetAvailabilityZones();

        for (const auto &zone: zones) {
            Aws::String stateString =
                    Aws::EC2::Model::AvailabilityZoneStateMapper::GetNameForAvailabilityZoneState(
                            zone.GetState());
            std::cout << std::left <<
                      std::setw(32) << zone.GetZoneName() <<
                      std::setw(20) << stateString <<
                      std::setw(32) << zone.GetRegionName() << std::endl;
        }
    }
    else {
        std::cerr << "Failed to describe availability zones:" <<
                  describe_outcome.GetError().GetMessage() << std::endl;
        result = false;
    }
    // snippet-end:[ec2.cpp.describe_zones.code]

    return result;

}

/*
 *
 *  main function
 *
 *  Usage: 'run_describe_regions_and_zones'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    (void) argc;
    (void) argv;

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::EC2::DescribeRegionsAndZones(clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

