// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.service;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import java.util.Map;

public class FargateDeployer {

    private static final String AWS_ACCOUNT_ID = "814548047983";
    private static final String AWS_REGION = "us-east-1";
    private static final String CLUSTER_NAME = "MyJavaWeathertopCluster";
    private static final String TASK_DEF_NAME = "WeathertopJava";
    private static final String SERVICE_NAME = "JavaWeathertop";
    private static final String CONTAINER_NAME = "weathertop-java";
    private static final String IMAGE = AWS_ACCOUNT_ID + ".dkr.ecr." + AWS_REGION + ".amazonaws.com/weathertop-java:latest";
    private static final String LOG_GROUP = "WeathertopContainerLogs";
    private static final String ROLE_NAME = "ecsTaskExecutionRole";
    private static final String[] SUBNETS = { "subnet-87bd1a89", "subnet-ef28c6b0" };
    private static final String[] SECURITY_GROUPS = { "sg-0e357c99b6b13bf62" };

    private final EcsClient ecsClient;
    private final IamClient iamClient;
    private final CloudWatchLogsClient logsClient;

    public FargateDeployer() {
        ecsClient = EcsClient.builder().region(software.amazon.awssdk.regions.Region.of(AWS_REGION)).build();
        iamClient = IamClient.builder().region(software.amazon.awssdk.regions.Region.AWS_GLOBAL).build();
        logsClient = CloudWatchLogsClient.builder().region(software.amazon.awssdk.regions.Region.of(AWS_REGION)).build();
    }

    public void deploy() {
        ensureIamRole();
        String clusterArn = ensureCluster();
        ensureLogGroup();
        String taskDefArn = registerTaskDefinition();
        createOrUpdateService(clusterArn, taskDefArn);
        System.out.println("✅ ECS Fargate deployment complete.");
    }

    private void ensureIamRole() {
        try {
            iamClient.getRole(GetRoleRequest.builder().roleName(ROLE_NAME).build());
            System.out.println("IAM Role exists: " + ROLE_NAME);
        } catch (NoSuchEntityException e) {
            System.out.println("Creating IAM Role: " + ROLE_NAME);
            String assumeRolePolicy = "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Effect\": \"Allow\", \"Principal\": { \"Service\": \"ecs-tasks.amazonaws.com\" }, \"Action\": \"sts:AssumeRole\" }] }";
            iamClient.createRole(CreateRoleRequest.builder()
                    .roleName(ROLE_NAME)
                    .assumeRolePolicyDocument(assumeRolePolicy)
                    .description("ECS Task Execution Role for Fargate")
                    .build());
            iamClient.attachRolePolicy(AttachRolePolicyRequest.builder()
                    .roleName(ROLE_NAME)
                    .policyArn("arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy")
                    .build());
        }
    }

    private String ensureCluster() {
        DescribeClustersResponse desc = ecsClient.describeClusters(DescribeClustersRequest.builder().clusters(CLUSTER_NAME).build());
        if (!desc.clusters().isEmpty() && desc.clusters().get(0).status().equals("ACTIVE")) {
            System.out.println("Cluster exists: " + CLUSTER_NAME);
            return desc.clusters().get(0).clusterArn();
        }
        CreateClusterResponse createResp = ecsClient.createCluster(CreateClusterRequest.builder().clusterName(CLUSTER_NAME).build());
        System.out.println("Created cluster: " + createResp.cluster().clusterArn());
        return createResp.cluster().clusterArn();
    }

    private void ensureLogGroup() {
        try {
            logsClient.describeLogGroups(DescribeLogGroupsRequest.builder().logGroupNamePrefix(LOG_GROUP).build())
                    .logGroups().stream()
                    .filter(g -> g.logGroupName().equals(LOG_GROUP))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Log group not found"));

            System.out.println("Log group exists: " + LOG_GROUP);
        } catch (RuntimeException e) {
            System.out.println("Creating log group: " + LOG_GROUP);
            logsClient.createLogGroup(CreateLogGroupRequest.builder().logGroupName(LOG_GROUP).build());
        }
    }

    private String registerTaskDefinition() {
        RegisterTaskDefinitionResponse resp = ecsClient.registerTaskDefinition(RegisterTaskDefinitionRequest.builder()
                .family(TASK_DEF_NAME)
                .networkMode(NetworkMode.AWSVPC)
                .requiresCompatibilities(Compatibility.FARGATE)
                .cpu("256")
                .memory("512")
                .executionRoleArn("arn:aws:iam::" + AWS_ACCOUNT_ID + ":role/" + ROLE_NAME)
                .containerDefinitions(ContainerDefinition.builder()
                        .name(CONTAINER_NAME)
                        .image(IMAGE)
                        .cpu(256)
                        .memory(512)
                        .essential(true)
                        .logConfiguration(LogConfiguration.builder()
                                .logDriver(LogDriver.AWSLOGS)
                                .options(
                                        Map.of(
                                                "awslogs-group", LOG_GROUP,
                                                "awslogs-region", AWS_REGION,
                                                "awslogs-stream-prefix", "ecs"
                                        )
                                ).build())
                        .build())
                .build());
        System.out.println("Registered task definition: " + resp.taskDefinition().taskDefinitionArn());
        return resp.taskDefinition().taskDefinitionArn();
    }

    private void createOrUpdateService(String clusterArn, String taskDefArn) {
        try {
            DescribeServicesResponse desc = ecsClient.describeServices(DescribeServicesRequest.builder()
                    .cluster(CLUSTER_NAME)
                    .services(SERVICE_NAME)
                    .build());

            if (!desc.services().isEmpty() && desc.services().get(0).status().equals("ACTIVE")) {
                System.out.println("Service exists, updating...");
                ecsClient.updateService(UpdateServiceRequest.builder()
                        .cluster(CLUSTER_NAME)
                        .service(SERVICE_NAME)
                        .taskDefinition(taskDefArn)
                        .desiredCount(1)
                        .build());
                return;
            }
        } catch (Exception e) {
            // Service does not exist or error, proceed to create
        }

        System.out.println("Creating new service...");
        ecsClient.createService(CreateServiceRequest.builder()
                .cluster(CLUSTER_NAME)
                .serviceName(SERVICE_NAME)
                .taskDefinition(taskDefArn)
                .desiredCount(1)
                .launchType(LaunchType.FARGATE)
                .networkConfiguration(NetworkConfiguration.builder()
                        .awsvpcConfiguration(AwsVpcConfiguration.builder()
                                .subnets(SUBNETS)
                                .securityGroups(SECURITY_GROUPS)
                                .assignPublicIp(AssignPublicIp.ENABLED)
                                .build())
                        .build())
                .build());
    }
}
