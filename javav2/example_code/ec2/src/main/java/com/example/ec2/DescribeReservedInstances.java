// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.describe_reserved_instances.main]
// snippet-start:[ec2.java2.describe_reserved_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesResponse;
import software.amazon.awssdk.services.ec2.model.ReservedInstances;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.describe_reserved_instances.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeReservedInstances {
    public static void main(String[] args) {
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            CompletableFuture<Void> future = describeReservedEC2InstancesAsync(ec2AsyncClient);
            future.join(); // Wait for the async operation to complete.
            System.out.println("Reserved EC2 Instances described successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    public static CompletableFuture<Void> describeReservedEC2InstancesAsync(Ec2AsyncClient ec2AsyncClient) {
        CompletableFuture<DescribeReservedInstancesResponse> response = ec2AsyncClient.describeReservedInstances();
        response.whenComplete((reservedInstancesResponse, ex) -> {
            if (reservedInstancesResponse != null) {
                reservedInstancesResponse.reservedInstances().forEach(instance -> {
                    System.out.printf(
                        "Found a Reserved Instance with id %s, " +
                            "in AZ %s, " +
                            "type %s, " +
                            "state %s%n",
                        instance.reservedInstancesId(),
                        instance.availabilityZone(),
                        instance.instanceType(),
                        instance.state().name());
                });
            } else {
                throw new RuntimeException("Failed to describe EC2 reserved instances.", ex);
            }
        });

        // Return CompletableFuture<Void> to signify the async operation's completion.
        return response.thenApply(resp -> null);
    }

}
// snippet-end:[ec2.java2.describe_reserved_instances.main]
