#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import "source-map-support/register";
import * as cdk from "aws-cdk-lib";
import { Stack, StackProps, aws_kinesisfirehose as firehose, aws_s3 as s3, aws_iam as iam, CfnOutput } from "aws-cdk-lib";
import { Construct } from "constructs";
import * as fs from 'fs';
import * as yaml from 'yaml';
import { CloudFormation } from 'aws-sdk';

class FirehoseStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const uniqueId = cdk.Names.uniqueId(this);
    const bucketName = `my-firehose-bucket-${uniqueId}`;

    const bucket = new s3.Bucket(this, "MyFirehoseBucket", {
      bucketName: bucketName,
      removalPolicy: cdk.RemovalPolicy.DESTROY, // Change to RETAIN for production
    });

    const firehoseRole = new iam.Role(this, "MyFirehoseRole", {
      assumedBy: new iam.ServicePrincipal("firehose.amazonaws.com"),
    });

    firehoseRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        "s3:AbortMultipartUpload",
        "s3:GetBucketLocation",
        "s3:GetObject",
        "s3:ListBucket",
        "s3:ListBucketMultipartUploads",
        "s3:PutObject"
      ],
      resources: [bucket.bucketArn, `${bucket.bucketArn}/*`]
    }));

    const deliveryStream = new firehose.CfnDeliveryStream(this, "MyFirehoseDeliveryStream", {
      deliveryStreamType: "DirectPut",
      s3DestinationConfiguration: {
        bucketArn: bucket.bucketArn,
        roleArn: firehoseRole.roleArn,
      },
    });

    new CfnOutput(this, "FirehoseStreamNameOutput", {
      value: deliveryStream.ref,
      description: "The name of the Kinesis Firehose delivery stream",
      exportName: "FirehoseStreamName",
    });

    new CfnOutput(this, "BucketNameOutput", {
      value: bucket.bucketName,
      description: "The name of the S3 bucket for Firehose",
      exportName: "BucketName",
    });

    new CfnOutput(this, "RoleArnOutput", {
      value: firehoseRole.roleArn,
      description: "The ARN of the IAM role for Firehose",
      exportName: "RoleArn",
    });
  }
}

const app = new cdk.App();
const stack = new FirehoseStack(app, "FirehoseStack", {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT!,
    region: process.env.CDK_DEFAULT_REGION!,
  },
});
app.synth();

async function saveOutputsToYaml() {
  const cloudFormation = new CloudFormation();
  const result = await cloudFormation.describeStacks({ StackName: stack.stackName }).promise();
  const outputs = result.Stacks[0].Outputs.reduce((acc, output) => {
    acc[output.OutputKey] = output.OutputValue;
    return acc;
  }, {});

  const yamlStr = yaml.stringify(outputs);
  fs.writeFileSync('cdk-outputs.yaml', yamlStr, 'utf8');
  console.log(`Outputs saved to cdk-outputs.yaml`);
}

saveOutputsToYaml().catch(err => {
  console.error(err);
  process.exit(1);
});
