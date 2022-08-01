// snippet-sourcedescription:[DescribeRegionsAndZones.kt demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) Regions and Zones.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_region_and_zones.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeAvailabilityZonesRequest
import aws.sdk.kotlin.services.ec2.model.DescribeRegionsRequest
// snippet-end:[ec2.kotlin.describe_region_and_zones.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    describeEC2RegionsAndZones()
}

// snippet-start:[ec2.kotlin.describe_region_and_zones.main]
suspend fun describeEC2RegionsAndZones() {

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val regionsResponse = ec2.describeRegions(DescribeRegionsRequest {})
        regionsResponse.regions?.forEach { region ->
            println("Found Region ${region.regionName} with endpoint ${region.endpoint}")
        }

        val zonesResponse = ec2.describeAvailabilityZones(DescribeAvailabilityZonesRequest {})
        zonesResponse.availabilityZones?.forEach { zone ->
            println("Found Availability Zone ${zone.zoneName} with status  ${zone.state} in Region ${zone.regionName}")
        }
    }
}
// snippet-end:[ec2.kotlin.describe_region_and_zones.main]
