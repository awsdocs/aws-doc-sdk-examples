// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import * as cdk from "aws-cdk-lib";
import * as events from "aws-cdk-lib/aws-events";
import * as targets from "aws-cdk-lib/aws-events-targets";
import * as iam from "aws-cdk-lib/aws-iam";
import * as path from "path";
import * as lambda from "aws-cdk-lib/aws-lambda";
import { Duration, Stack, StackProps } from "aws-cdk-lib";
import { Construct } from "constructs";
import { DockerImageCode, DockerImageFunction } from "aws-cdk-lib/aws-lambda";

export interface NukeStackProps extends cdk.StackProps {
  awsNukeDryRunFlag?: string;
  awsNukeVersion?: string;
  owner?: string;
}

class NukeStack extends cdk.Stack {
  private readonly nukeLambdaRole: iam.Role;

  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    // Lambda Function role
    this.nukeLambdaRole = new iam.Role(this, "NukeLambdaRole", {
      assumedBy: new iam.ServicePrincipal("lambda.amazonaws.com"),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName("AdministratorAccess"),
      ],
    });

    // Create the Lambda function
    const lambdaFunction = new DockerImageFunction(
      this,
      "docker-lambda-function",
      {
        functionName: "docker-lambda-fn",
        code: DockerImageCode.fromImageAsset(path.join(__dirname)),
        memorySize: 1024,
        timeout: Duration.minutes(15),
        architecture: lambda.Architecture.ARM_64,
        description: "This is dockerized AWS Lambda function",
        role: this.nukeLambdaRole,
      },
    );

    // Create EventBridge rule to trigger the Lambda function weekly
    const rule = new events.Rule(this, "WeeklyTriggerRule", {
      schedule: events.Schedule.expression("cron(0 0 ? * SUN *)"), // Runs at 00:00 every Sunday
    });

    // Add the Lambda function as a target for the EventBridge rule
    rule.addTarget(new targets.LambdaFunction(lambdaFunction));
  }
}

const app = new cdk.App();
new NukeStack(app, "NukeStack", {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
  terminationProtection: true,
});
