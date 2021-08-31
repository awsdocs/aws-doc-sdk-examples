//snippet-sourcedescription:[DescribeRegionsAndZones.kt demonstrates how to get information about all the Amazon Elastic Compute Cloud (Amazon EC2) Regions and Zones.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/21/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_region_and_zones.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeRegionsRequest
import aws.sdk.kotlin.services.ec2.model.Region
import aws.sdk.kotlin.services.ec2.model.DescribeAvailabilityZonesRequest
import aws.sdk.kotlin.services.ec2.model.AvailabilityZone
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_region_and_zones.import]

suspend fun main() {

    val ec2Client = Ec2Client{region = "us-east-1"}
    describeEC2RegionsAndZones(ec2Client)
    ec2Client.close()
}

// snippet-start:[ec2.kotlin.describe_region_and_zones.main]
suspend fun describeEC2RegionsAndZones(ec2: Ec2Client) {

    try {
        val regionsResponse = ec2.describeRegions(DescribeRegionsRequest{})
        for (region: Region in regionsResponse.regions!!)
            println("Found Region ${region.regionName} with endpoint ${region.endpoint}")


        val zonesResponse = ec2.describeAvailabilityZones(DescribeAvailabilityZonesRequest{})
        for (zone: AvailabilityZone in zonesResponse.availabilityZones!!)
            println("Found Availability Zone ${zone.zoneName} with status  ${zone.state} in region ${zone.regionName}")

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.describe_region_and_zones.main]