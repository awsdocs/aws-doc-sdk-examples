/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.ec2;
import software.amazon.awssdk.services.ec2.EC2Client;
import software.amazon.awssdk.services.ec2.model.DescribeRegionsResponse;
import software.amazon.awssdk.services.ec2.model.Region;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;

/**
 * Describes all regions and zones
 */
public class DescribeRegionsAndZones
{
    public static void main(String[] args)
    {
        EC2Client ec2 = EC2Client.create();

        DescribeRegionsResponse regions_response = ec2.describeRegions();

        for(Region region : regions_response.regions()) {
            System.out.printf(
                "Found region %s " +
                "with endpoint %s",
                region.regionName(),
                region.endpoint());
            System.out.println();
        }

        DescribeAvailabilityZonesResponse zones_response =
            ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.availabilityZones()) {
            System.out.printf(
                "Found availability zone %s " +
                "with status %s " +
                "in region %s",
                zone.zoneName(),
                zone.state(),
                zone.regionName());
            System.out.println();

        }
    }
}

