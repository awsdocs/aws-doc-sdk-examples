// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.describe_region_and_zones.complete]
// snippet-start:[ec2.java2.describe_region_and_zones.main]
// snippet-start:[ec2.java2.describe_region_and_zones.import]
// snippet-start:[ec2.java2.describe_region_and_zones.avail_zone]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.DescribeRegionsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;
import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.describe_region_and_zones.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeRegionsAndZones {
    public static void main(String[] args) {
        // snippet-start:[ec2.java2.describe_region_and_zones.client]
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();
        // snippet-end:[ec2.java2.describe_region_and_zones.client]

        try {
            CompletableFuture<Void> future = describeEC2RegionsAndZonesAsync(ec2AsyncClient);
            future.join(); // Wait for both async operations to complete.
            System.out.println("EC2 Regions and Availability Zones described successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    public static CompletableFuture<Void> describeEC2RegionsAndZonesAsync(Ec2AsyncClient ec2AsyncClient) {
        CompletableFuture<DescribeRegionsResponse> regionsResponse = ec2AsyncClient.describeRegions();
        CompletableFuture<DescribeRegionsResponse> regionsFuture = regionsResponse.whenComplete((regionsResp, ex) -> {
            if (regionsResp != null) {
                regionsResp.regions().forEach(region -> {
                    System.out.printf(
                        "Found Region %s with endpoint %s%n",
                        region.regionName(),
                        region.endpoint());
                    System.out.println();
                });
            } else {
                throw new RuntimeException("Failed to describe EC2 regions.", ex);
            }
        });

        CompletableFuture<DescribeAvailabilityZonesResponse> zonesResponse = ec2AsyncClient.describeAvailabilityZones();
        CompletableFuture<DescribeAvailabilityZonesResponse> zonesFuture = zonesResponse.whenComplete((zonesResp, ex) -> {
            if (zonesResp != null) {
                zonesResp.availabilityZones().forEach(zone -> {
                    System.out.printf(
                        "Found Availability Zone %s with status %s in region %s%n",
                        zone.zoneName(),
                        zone.state(),
                        zone.regionName()
                    );
                    System.out.println();
                });
            } else {
                throw new RuntimeException("Failed to describe EC2 availability zones.", ex);
            }
        });

        // Combine both CompletableFuture<Void> into a single CompletableFuture<Void>.
        return CompletableFuture.allOf(regionsFuture, zonesFuture);
    }
}
// snippet-end:[ec2.java2.describe_region_and_zones.main]
// snippet-end:[ec2.java2.describe_region_and_zones.region]
// snippet-end:[ec2.java2.describe_region_and_zones.avail_zone]
// snippet-end:[ec2.java2.describe_region_and_zones.complete]
