// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.autoscaling;

// snippet-start:[autoscale.java2.detach.main]
// snippet-start:[autoscale.java2.detach.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.autoscaling.model.DetachInstancesRequest;
// snippet-end:[autoscale.java2.detach.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetachInstances {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <groupName> <instanceId>

                Where:
                    groupName - The name of the Auto Scaling group.
                    instanceId - The instance Id value.
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String groupName = args[0];
        String instanceId = args[1];
        AutoScalingClient autoScalingClient = AutoScalingClient.builder()
                .region(Region.US_EAST_1)
                .build();

        detachInstance(autoScalingClient, groupName, instanceId);
        autoScalingClient.close();
    }

    public static void detachInstance(AutoScalingClient autoScalingClient, String groupName, String instanceId) {
        try {
            DetachInstancesRequest detachInstancesRequest = DetachInstancesRequest.builder()
                    .autoScalingGroupName(groupName)
                    .shouldDecrementDesiredCapacity(false)
                    .instanceIds(instanceId)
                    .build();

            autoScalingClient.detachInstances(detachInstancesRequest);
            System.out.println("You have detached instance " + instanceId + " from " + groupName);

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[autoscale.java2.detach.main]
