// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as cdk from "aws-cdk-lib";
import * as codebuild from "aws-cdk-lib/aws-codebuild";
import * as iam from "aws-cdk-lib/aws-iam";
import * as s3 from "aws-cdk-lib/aws-s3";
import * as sns from "aws-cdk-lib/aws-sns";
import * as events from "aws-cdk-lib/aws-events";
import * as targets from "aws-cdk-lib/aws-events-targets";
import * as stepfunctions from "aws-cdk-lib/aws-stepfunctions";
import * as tasks from "aws-cdk-lib/aws-stepfunctions-tasks";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as batch from "aws-cdk-lib/aws-batch";
import * as sqs from "aws-cdk-lib/aws-sqs";
import * as lambda from "aws-cdk-lib/aws-lambda";
import { SqsEventSource } from "aws-cdk-lib/aws-lambda-event-sources";
import * as subs from "aws-cdk-lib/aws-sns-subscriptions";
import { Construct } from "constructs";
import { readAccountConfig } from "../../config/targets";
import { readResourceConfig } from "../../config/resources";
import variableConfigJson from "../../config/variables.json";

const toolName = process.env.TOOL_NAME ?? "defaultToolName";

export class PluginStack extends cdk.Stack {
  private awsRegion: string;
  private adminAccountId: string;
  private batchMemory: string;
  private batchVcpus: string;

  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Configurations for Plugin-specific logic
    const acctConfig = readAccountConfig("../../config/targets.yaml");
    const resourceConfig = readResourceConfig("../../config/resources.yaml");

    this.awsRegion = resourceConfig["aws_region"];
    this.adminAccountId = resourceConfig["admin_acct"];
    const adminTopicName = resourceConfig["topic_name"];
    const adminBucketName = resourceConfig["bucket_name"];

    const snsTopic = this.initGetTopic(adminTopicName);
    const sqsQueue = new sqs.Queue(this, `BatchJobQueue-${toolName}`);
    if (acctConfig[`${toolName}`].status === "enabled") {
      this.initSubscribeSns(sqsQueue, snsTopic);
      this.batchMemory = acctConfig[`${toolName}`]?.memory ?? "16384"; // MiB
      this.batchVcpus = acctConfig[`${toolName}`]?.vcpus ?? "4"; // CPUs
    }

    const [jobDefinition, jobQueue] = this.initBatchFargate();
    const batchFunction = this.initBatchLambda(jobQueue, jobDefinition);
    this.initSqsLambdaIntegration(batchFunction, sqsQueue);

    if (acctConfig[`${toolName}`].status === "enabled") {
      this.initLogFunction(adminBucketName);
    }

    // Nuke Account Cleansing logic
    const bucketName = "nuke-account-cleanser-config";
    const nukeRoleName = "nuke-auto-account-cleanser";
    const dryRunFlag = "true";
    const nukeVersion = "2.21.2";
    const owner = "OpsAdmin";

    // S3 Bucket
    const nukeBucket = new s3.Bucket(this, "NukeBucket", {
      bucketName: `${bucketName}-${cdk.Aws.ACCOUNT_ID}-${cdk.Aws.REGION}`,
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
      removalPolicy: cdk.RemovalPolicy.RETAIN,
    });

    // SNS Topic
    const nukeTopic = new sns.Topic(this, "NukeTopic", {
      topicName: "nuke-cleanser-notify-topic",
    });

    // IAM Role for CodeBuild
    const codebuildRole = new iam.Role(this, "CodeBuildRole", {
      assumedBy: new iam.ServicePrincipal("codebuild.amazonaws.com"),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("AdministratorAccess"),
      ],
    });

    // CodeBuild Project
    const codebuildProject = new codebuild.Project(this, "NukeCodeBuild", {
      projectName: "AccountNuker",
      source: codebuild.Source.s3({
        bucket: nukeBucket,
        path: "buildspec.yml",
      }),
      environment: {
        buildImage: codebuild.LinuxBuildImage.STANDARD_5_0,
        computeType: codebuild.ComputeType.SMALL,
        privileged: true,
      },
      role: codebuildRole,
      environmentVariables: {
        AWS_NukeDryRun: { value: dryRunFlag },
        AWS_NukeVersion: { value: nukeVersion },
      },
    });

    // Step Functions Role
    const stepFunctionsRole = new iam.Role(this, "StepFunctionsRole", {
      assumedBy: new iam.ServicePrincipal("states.amazonaws.com"),
      inlinePolicies: {
        StepFunctionPolicy: new iam.PolicyDocument({
          statements: [
            new iam.PolicyStatement({
              actions: ["codebuild:StartBuild"],
              resources: [codebuildProject.projectArn],
            }),
            new iam.PolicyStatement({
              actions: ["sns:Publish"],
              resources: [nukeTopic.topicArn],
            }),
          ],
        }),
      },
    });

    // Step Functions State Machine
    const codeBuildTask = new tasks.CodeBuildStartBuild(this, "CodeBuildTask", {
      project: codebuildProject,
      environmentVariablesOverride: {
        Region: tasks.TaskInput.fromDataAt("$.region_id"),
      },
    });

    const stateMachineDefinition = new stepfunctions.Map(this, "MapState", {
      itemsPath: stepfunctions.JsonPath.stringAt("$.InputPayLoad.region_list"),
      parameters: {
        region_id: stepfunctions.JsonPath.stringAt("$$.Map.Item.Value"),
        nuke_dry_run: stepfunctions.JsonPath.stringAt("$.InputPayLoad.nuke_dry_run"),
        nuke_version: stepfunctions.JsonPath.stringAt("$.InputPayLoad.nuke_version"),
      },
    }).iterator(codeBuildTask); // Correct use of the Map iterator


    const stateMachine = new stepfunctions.StateMachine(this, "NukeStateMachine", {
      stateMachineName: "NukeAccountCleanser",
      definition: stateMachineDefinition,
      role: stepFunctionsRole,
    });

    // EventBridge Rule for Nuke Schedule
    new events.Rule(this, "EventBridgeRule", {
      schedule: events.Schedule.cron({ minute: "0", hour: "7" }),
      targets: [new targets.SfnStateMachine(stateMachine)],
    });

    // Outputs
    new cdk.CfnOutput(this, "NukeBucketName", { value: nukeBucket.bucketName });
    new cdk.CfnOutput(this, "NukeTopicArn", { value: nukeTopic.topicArn });
    new cdk.CfnOutput(this, "AdminBucketName", { value: adminBucketName });
    new cdk.CfnOutput(this, "AdminTopicArn", { value: snsTopic.topicArn });
  }

  private initGetTopic(topicName: string): sns.ITopic {
    const externalSnsTopicArn = `arn:aws:sns:${this.awsRegion}:${this.adminAccountId}:${topicName}`;
    return sns.Topic.fromTopicArn(this, "ExternalSNSTopic", externalSnsTopicArn);
  }

  private initBatchFargate(): [batch.CfnJobDefinition, batch.CfnJobQueue] {
    const batchExecutionRole = new iam.Role(this, `BatchExecutionRole-${toolName}`, {
      assumedBy: new iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
      roleName: `BatchExecutionRole-${toolName}`,
      inlinePolicies: {
        BatchLoggingPolicy: new iam.PolicyDocument({
          statements: [
            new iam.PolicyStatement({
              effect: iam.Effect.ALLOW,
              actions: [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents",
                "logs:DescribeLogStreams",
              ],
              resources: ["arn:aws:logs:*:*:*"],
            }),
          ],
        }),
      },
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("AdministratorAccess"),
        iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonEC2ContainerRegistryReadOnly"),
        iam.ManagedPolicy.fromAwsManagedPolicyName(
          "service-role/AmazonECSTaskExecutionRolePolicy"
        ),
      ],
    });

    const vpc = ec2.Vpc.fromLookup(this, "Vpc", { isDefault: true });
    const sg = new ec2.SecurityGroup(this, "sg", {
      securityGroupName: "batch-sg",
      vpc,
    });

    const fargateEnvironment = new batch.CfnComputeEnvironment(
      this,
      `FargateEnv-${toolName}`,
      {
        type: "MANAGED",
        computeResources: {
          type: "FARGATE",
          subnets: vpc.selectSubnets().subnetIds,
          securityGroupIds: [sg.securityGroupId],
          maxvCpus: 1,
        },
      }
    );

    const containerImageUri = `${this.adminAccountId}.dkr.ecr.${this.awsRegion}.amazonaws.com/${toolName}:latest`;

    const jobDefinition = new batch.CfnJobDefinition(this, "JobDefn", {
      type: "container",
      containerProperties: {
        image: containerImageUri,
        jobRoleArn: batchExecutionRole.roleArn,
        executionRoleArn: batchExecutionRole.roleArn,
        networkConfiguration: { assignPublicIp: "ENABLED" },
        resourceRequirements: [
          { type: "VCPU", value: this.batchVcpus },
          { type: "MEMORY", value: this.batchMemory },
        ],
        environment: variableConfigJson,
      },
      platformCapabilities: ["FARGATE"],
    });

    const jobQueue = new batch.CfnJobQueue(this, `JobQueue-${toolName}`, {
      priority: 0,
      computeEnvironmentOrder: [
        { computeEnvironment: fargateEnvironment.ref, order: 0 },
      ],
    });

    return [jobDefinition, jobQueue];
  }

  private initSubscribeSns(sqsQueue: sqs.Queue, snsTopic: sns.ITopic): void {
    snsTopic.addSubscription(
      new subs.SqsSubscription(sqsQueue, { rawMessageDelivery: true })
    );
  }

  private initBatchLambda(
    jobQueue: batch.CfnJobQueue,
    jobDefinition: batch.CfnJobDefinition
  ): lambda.Function {
    const executionRole = new iam.Role(this, `BatchLambdaExecutionRole-${toolName}`, {
      assumedBy: new iam.ServicePrincipal("lambda.amazonaws.com"),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
      ],
    });

    executionRole.addToPolicy(
      new iam.PolicyStatement({
        actions: ["batch:SubmitJob", "batch:DescribeJobs"],
        resources: ["*"],
      })
    );

    return new lambda.Function(this, `SubmitBatchJob-${toolName}`, {
      runtime: lambda.Runtime.PYTHON_3_8,
      handler: "submit_job.handler",
      code: lambda.Code.fromAsset("lambda"),
      environment: {
        JOB_QUEUE: jobQueue.ref,
        JOB_DEFINITION: jobDefinition.ref,
        JOB_NAME: `job-${toolName}`,
      },
      role: executionRole,
    });
  }

  private initSqsLambdaIntegration(
    lambdaFunction: lambda.Function,
    sqsQueue: sqs.Queue
  ): void {
    lambdaFunction.addEventSource(new SqsEventSource(sqsQueue));
    sqsQueue.grantConsumeMessages(lambdaFunction);
    lambdaFunction.addToRolePolicy(
      new iam.PolicyStatement({
        actions: ["sqs:ReceiveMessage"],
        resources: [sqsQueue.queueArn],
      })
    );
    lambdaFunction.addToRolePolicy(
      new iam.PolicyStatement({
        actions: [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
        ],
        resources: ["*"],
      })
    );
  }

  private initLogFunction(adminBucketName: string): void {
    const bucket = new s3.Bucket(this, "LogBucket", {
      versioned: false,
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
    });

    const executionRole = new iam.Role(this, "CloudWatchExecutionRole", {
      assumedBy: new iam.ServicePrincipal("lambda.amazonaws.com"),
      description: "Allows Lambda function to get logs from CloudWatch",
      roleName: "CloudWatchExecutionRole",
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
      ],
    });

    bucket.addToResourcePolicy(
      new iam.PolicyStatement({
        actions: ["s3:PutObject", "s3:PutObjectAcl", "s3:DeleteObject", "s3:ListBucket", "s3:GetObject"],
        resources: [`${bucket.bucketArn}/*`, bucket.bucketArn],
        principals: [new iam.ArnPrincipal(executionRole.roleArn)],
      })
    );

    executionRole.addToPolicy(
      new iam.PolicyStatement({
        actions: ["logs:GetLogEvents", "logs:DescribeLogStreams"],
        resources: [`arn:aws:logs:${this.awsRegion}:${cdk.Aws.ACCOUNT_ID}:*`],
      })
    );

    const lambdaFunction = new lambda.Function(this, "BatchJobCompleteLambda", {
      runtime: lambda.Runtime.PYTHON_3_8,
      handler: "export_logs.handler",
      code: lambda.Code.fromAsset("lambda"),
      timeout: cdk.Duration.seconds(60),
      environment: {
        TOOL_NAME: toolName,
        LOCAL_BUCKET_NAME: bucket.bucketName,
        ADMIN_BUCKET_NAME: adminBucketName,
      },
    });

    const batchRule = new events.Rule(this, "BatchAllEventsRule", {
      eventPattern: { source: ["aws.batch"] },
    });

    batchRule.addTarget(new targets.LambdaFunction(lambdaFunction));
  }
}

const app = new cdk.App();
new PluginStack(app, "PluginStack", {
  env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: process.env.CDK_DEFAULT_REGION },
});
app.synth();
