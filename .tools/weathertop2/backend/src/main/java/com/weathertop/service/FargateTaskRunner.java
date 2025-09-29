// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;
import java.util.List;

public class FargateTaskRunner {

    private static final Region REGION = Region.US_EAST_1;

    // Network config matching Python exactly
    private static final List<String> SUBNETS = List.of(
            "subnet-03c28397a3a7cd314",
            "subnet-06dde61595900f899"
    );

    private static final List<String> SECURITY_GROUPS = List.of(
            "sg-0e357c99b6b13bf62"
    );

    /**
     * Runs a Fargate task using the specified task definition in Amazon ECS.
     *
     * @param taskDefinition The ARN or family name of the task definition to use.
     * @return The ARN of the task that was started, or null if the task failed to start.
     */
    public String runFargateTask(String taskDefinition, String clusterName ) {
        EcsClient ecsClient = EcsClient.builder().region(REGION).build();

        AwsVpcConfiguration vpcConfig = AwsVpcConfiguration.builder()
                .subnets(SUBNETS)
                .securityGroups(SECURITY_GROUPS)
                .assignPublicIp(AssignPublicIp.ENABLED)
                .build();

        NetworkConfiguration netConfig = NetworkConfiguration.builder()
                .awsvpcConfiguration(vpcConfig)
                .build();

        RunTaskRequest request = RunTaskRequest.builder()
                .cluster(clusterName)
                .launchType(LaunchType.FARGATE)
                .taskDefinition(taskDefinition)
                .networkConfiguration(netConfig)
                .count(1)
                .build();

        RunTaskResponse response = ecsClient.runTask(request);

        if (!response.tasks().isEmpty()) {
            String taskArn = response.tasks().get(0).taskArn();
            System.out.println("🚀 Started task: " + taskArn);
            return taskArn;
        } else {
            System.err.println("❌ Run task failed: " + response.failures());
            return null;
        }
    }
}
