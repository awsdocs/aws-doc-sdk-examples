package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.constructs.Construct;

import java.util.List;

public class EcsStack extends Stack {
    public EcsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // IAM Role for ECS Tasks
        Role ecsRole = Role.Builder.create(this, "RoleEcs")
            .assumedBy(new ServicePrincipal("ecs-tasks.amazonaws.com"))
            .build();

        // Attach the AWS Batch Full Access managed policy
        ecsRole.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName("AWSBatchFullAccess"));

          // Add policies to the IAM role
        ecsRole.addToPolicy(PolicyStatement.Builder.create()
            .effect(Effect.ALLOW)
            .actions(List.of(
                "batch:CreateComputeEnvironment",
                "batch:UpdateComputeEnvironment",
                "batch:DeleteComputeEnvironment",
                "batch:SubmitJob",
                "batch:DescribeComputeEnvironments",
                "batch:DescribeJobDefinitions",
                "batch:DescribeJobQueues",
                "batch:DescribeJobs"
            ))
            .resources(List.of("*"))
            .build());

        ecsRole.addToPolicy(PolicyStatement.Builder.create()
            .effect(Effect.ALLOW)
            .actions(List.of(
                "ecs:ListClusters",
                "ecs:DescribeClusters",
                "ecs:ListContainerInstances",
                "ecs:DescribeContainerInstances",
                "ecs:ListTaskDefinitions",
                "ecs:CreateCluster",
                "ecs:DeleteCluster",
                "ecs:DescribeTaskDefinitions",
                "ecs:ListTasks",
                "ecs:DescribeTasks",
                "ecr:GetAuthorizationToken",
                "ecr:BatchGetImage",
                "ecr:GetDownloadUrlForLayer",
                "ecr:DescribeRepositories",
                "ecr:ListImages",
                "ecs:RunTask",
                "ecs:StartTask",
                "ecs:StopTask",
                "ecs:UpdateService",
                "ecs:RegisterTaskDefinition",
                "ecs:DeregisterTaskDefinition",
                "ecs:SubmitTaskStateChange",
                "ecs:SubmitContainerStateChange",
                "ecs:DescribeServices",
                "ecs:ListServices",
                "logs:DescribeLogGroups",
                "logs:DescribeLogStreams",
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents",
                "ec2:DescribeSubnets",
                "sts:AssumeRole",
                "iam:PassRole"
            ))
            .resources(List.of("*"))
            .build());


        // Output the role ARN
        new CfnOutput(this, "EcsRoleArn", CfnOutputProps.builder()
            .value(ecsRole.getRoleArn())
            .description("The ARN of the ECS Role")
            .build());
    }
}
