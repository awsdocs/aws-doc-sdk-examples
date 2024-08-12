// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.running_instances.main]
// snippet-start:[ec2.java2.running_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.running_instances.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FindRunningInstances {
    public static void main(String[] args) {
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();
        try {
            CompletableFuture<Void> future = findRunningEC2InstancesUsingPaginatorAsync(ec2AsyncClient);
            future.join(); // Wait for the async operation to complete.
            System.out.println("Running EC2 Instances described successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    public static CompletableFuture<Void> findRunningEC2InstancesUsingPaginatorAsync(Ec2AsyncClient ec2AsyncClient) {
        // Create a DescribeInstancesRequest to filter running instances.
        DescribeInstancesRequest describeInstancesRequest = DescribeInstancesRequest.builder()
            .filters(f -> f.name("instance-state-name").values("running"))
            .build();

        // Fetch instances asynchronously.
        CompletableFuture<DescribeInstancesResponse> response = ec2AsyncClient.describeInstances(describeInstancesRequest);
        response.whenComplete((instancesResponse, ex) -> {
            if (instancesResponse != null) {
                instancesResponse.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .forEach(instance -> System.out.println("Instance ID: " + instance.instanceId() + ", State: " + instance.state().name()));
            } else {
                throw new RuntimeException("Failed to describe running EC2 instances.", ex);
            }
        });

        // Return CompletableFuture<Void> to signify the async operation's completion.
        return response.thenApply(resp -> null);
    }
}
// snippet-end:[ec2.java2.running_instances.main]