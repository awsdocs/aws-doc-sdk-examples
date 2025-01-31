// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

// snippet-start:[ec2.java2.reboot_instance.main]
// snippet-start:[ec2.java2.reboot_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.RebootInstancesResponse;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
import java.util.concurrent.CompletableFuture;
// snippet-end:[ec2.java2.reboot_instance.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RebootInstance {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                   <instanceId>\s

                Where:
                   instanceId - An instance id value that you can obtain from the AWS Console.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            CompletableFuture<Void> future = rebootEC2InstanceAsync(ec2AsyncClient, instanceId);
            future.join();
            System.out.println("Instance rebooted successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
    }

    /**
     * Reboots an Amazon EC2 instance asynchronously.
     *
     * @param ec2AsyncClient the EC2 async client to use for the operation
     * @param instanceId the ID of the EC2 instance to reboot
     * @return a {@link CompletableFuture} that completes when the reboot operation is finished
     */
    public static CompletableFuture<Void> rebootEC2InstanceAsync(Ec2AsyncClient ec2AsyncClient, String instanceId) {
        RebootInstancesRequest request = RebootInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();

        CompletableFuture<RebootInstancesResponse> response = ec2AsyncClient.rebootInstances(request);
        return response.whenComplete((result, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to reboot instance: " + instanceId, ex);
            } else if (result == null) {
                throw new RuntimeException("No response received for rebooting instance: " + instanceId);
            } else {
                System.out.printf("Successfully rebooted instance %s%n", instanceId);
            }
        }).thenApply(result -> null);
    }
}
// snippet-end:[ec2.java2.reboot_instance.main]
