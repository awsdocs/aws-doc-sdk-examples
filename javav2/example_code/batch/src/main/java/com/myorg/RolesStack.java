package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.constructs.Construct;
import software.amazon.awscdk.services.iam.Role;

public class RolesStack extends Stack {
    public RolesStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create the task execution role that grants ECS agents permission to make AWS API calls.
        Role ecsTaskExecutionRole = Role.Builder.create(this, "MyEcsTaskExecutionRole")
                .assumedBy(new ServicePrincipal("ecs-tasks.amazonaws.com"))
                .build();
        ecsTaskExecutionRole.addManagedPolicy(
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AmazonECSTaskExecutionRolePolicy"));

        // Create the service role used in the CreateComputeEnvironment operation for AWS Batch.
        Role batchServiceRole = Role.Builder.create(this, "MyBatchServiceRole")
                .assumedBy(new ServicePrincipal("batch.amazonaws.com"))
                .build();
        batchServiceRole.addManagedPolicy(
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSBatchServiceRole")
        );

        CfnOutput output1 = CfnOutput.Builder.create(this, "EcsRoleArn")
                .value(ecsTaskExecutionRole.getRoleArn())
                .description("The ARN of the ECS Role")
                .build();
        CfnOutput output2 = CfnOutput.Builder.create(this, "BatchRoleArn")
                .value(batchServiceRole.getRoleArn())
                .description("The ARN of the Batch Role")
                .build();

/*        // Output the role ARN
        new CfnOutput(this, "EcsRoleArn", CfnOutputProps.builder()
                .value(ecsTaskExecutionRole.getRoleArn())
                .description("The ARN of the ECS Role")
                .build());

        new CfnOutput(this, "BatchRoleArn", CfnOutputProps.builder()
                .value(batchServiceRole.getRoleArn())
                .description("The ARN of the Batch Role")
                .build());*/
    }
}
