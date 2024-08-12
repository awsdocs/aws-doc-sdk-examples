// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.monitor_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.MonitorInstancesRequest;
import software.amazon.awssdk.services.ec2.model.UnmonitorInstancesRequest;

import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.monitor_instance.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class MonitorInstance {

    public static void main(String[] args) {
        final String usage = """

                Usage:
                   <instanceId> <monitor>

                Where:
                   instanceId - An instance id value that you can obtain from the AWS Management Console.\s
                   monitor - A monitoring status (true|false)""";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        boolean monitor = Boolean.parseBoolean(args[1]);
        Region region = Region.US_EAST_1;
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(region)
            .build();

        CompletableFuture<Void> future;
        if (monitor) {
            future = monitorInstanceAsync(ec2AsyncClient, instanceId);
        } else {
            future = unmonitorInstanceAsync(ec2AsyncClient, instanceId);
        }

        future.join(); // Wait for the async operation to complete.
    }

    // snippet-start:[ec2.java2.monitor_instance.main]
    public static CompletableFuture<Void> monitorInstanceAsync(Ec2AsyncClient ec2AsyncClient, String instanceId) {
        MonitorInstancesRequest request = MonitorInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        // Monitor instances asynchronously
        CompletableFuture<Void> response = ec2AsyncClient.monitorInstances(request)
            .whenComplete((result, ex) -> {
                if (result != null) {
                    System.out.printf("Successfully enabled monitoring for instance %s%n", instanceId);
                } else {
                    throw new RuntimeException("Failed to enable monitoring for instance: " + instanceId, ex);
                }
            })
            .thenApply(result -> null);

        return response;
    }
    // snippet-end:[ec2.java2.monitor_instance.main]

    // snippet-start:[ec2.java2.monitor_instance.stop]
    public static CompletableFuture<Void> unmonitorInstanceAsync(Ec2AsyncClient ec2AsyncClient, String instanceId) {
        UnmonitorInstancesRequest request = UnmonitorInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        // Unmonitor instances asynchronously
        CompletableFuture<Void> response = ec2AsyncClient.unmonitorInstances(request)
            .whenComplete((result, ex) -> {
                if (result != null) {
                    System.out.printf("Successfully disabled monitoring for instance %s%n", instanceId);
                } else {
                    throw new RuntimeException("Failed to disable monitoring for instance: " + instanceId, ex);
                }
            })
            .thenApply(result -> null);

        return response;
    }
    // snippet-end:[ec2.java2.monitor_instance.stop]
}
