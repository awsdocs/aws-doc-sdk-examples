package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;
import software.constructs.Construct;
import java.util.List;

public class BatchStack extends Stack {
    public BatchStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id);

        // IAM Role for AWS Batch
        Role batchRole = Role.Builder.create(this, "RoleBatch")
            .assumedBy(new CompositePrincipal(
                new ServicePrincipal("batch.amazonaws.com")
            ))
            .build();

        // Attach the AWS Batch Full Access managed policy
        batchRole.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName("AWSBatchFullAccess"));

        // Add policies to the IAM role
        batchRole.addToPolicy(PolicyStatement.Builder.create()
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

        batchRole.addToPolicy(PolicyStatement.Builder.create()
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
        new CfnOutput(this, "BatchRoleArn", CfnOutputProps.builder()
            .value(batchRole.getRoleArn())
            .description("The ARN of the Batch Role")
            .build());
    }
}