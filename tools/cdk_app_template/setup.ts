#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// TODO: Customize this AWS Cloud Development Kit (AWS CDK) code for your
// specific AWS CDK solution. For more information, see the
// AWS CDK Developer Guide at
// https://docs.aws.amazon.com/cdk/latest/guide

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';

// Only include this import statement if your AWS CDK app needs to work
// with Amazon Simple Storage Service (Amazon S3) buckets. This import
// statement is included here only for this example. If you use this
// import statement, be sure to also run 'npm install @aws-cdk/aws-s3'.
import { Bucket } from '@aws-cdk/aws-s3';

// If you need more than just the Bucket class,
// you can import the entire Amazon S3 namespace as s3:
//   import * as s3 from '@aws-cdk/aws-s3';

// Only include this import statement if your AWS CDK app needs to get
// input from the caller (such as the name of a new Amazon S3 bucket
// to be created).
import { CfnParameter } from '@aws-cdk/core';

// Only include this import statement if your AWS CDK app needs to display
// output to the caller (such as the name of a new Amazon S3 bucket that
// was created).
import { CfnOutput } from '@aws-cdk/core';

// If you need both CfnParameter and CfnOutput,
// you can combine them into one import statement:
//   import { CfnOutput, CfnParameter } from '@aws-cdk/core';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // TODO: Add the code that defines your stack here.
    // For this example, here's how to create a bucket in Amazon S3.

    // This example names the bucket based on the caller's input.
    // Note that bucket names must be globally unique across all of AWS.
    // The caller can specify the name of the bucket in several ways:
    //
    // 1. When using the AWS Command Line Interface (AWS CLI), for example
    //    after running 'cdk synth > CloudFormation.yaml':
    //
    //    aws cloudformation create-stack \
    //      --template-body file://CloudFormation.yaml \
    //      --parameters ParameterKey=BucketName,ParameterValue=my-bucket-111111111111 \
    //      --stack-name SetupStack
    //
    // 2. When using the AWS CloudFormation console along with the
    //    AWS CloudFormation template that was created after running
    //    'cdk synth', by typing a value
    //    into the 'BucketName' field in the create stack wizard in the
    //    AWS CloudFormation console.
    //
    // 3. When using the AWS CDK after running 'cdk synth', for example:
    //
    //    cdk deploy --parameters BucketName=my-bucket-111111111111
    //
    const bucketName = new CfnParameter(this, 'BucketName', {
      type: 'String',
      description: 'The name of the Amazon S3 bucket to be created.'});

    // Use the caller-provided bucket name to create the bucket.
    let myBucket: Bucket = new Bucket(this, 'bucket', {
      bucketName: bucketName.valueAsString,
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });

    // ////////////////////////////////////////////////////////////////////////
    // Tip: Alternatively, you could hard-code the name of the bucket
    // into your CDK app's 'cdk.json' file's 'context' object, for example:
    //
    // {
    //   ... snip ...
    //   "context": {
    //     ... snip ...
    //     "bucketName": "my-bucket-111111111111"
    //   }
    // }
    //
    // And then you could change the preceding constant declaration to:
    //
    // const bucketName = this.node.tryGetContext('bucketName');
    // ////////////////////////////////////////////////////////////////////////

    // The caller can confirm the name of the bucket that was created in several ways:
    //
    // 1. When using the AWS CLI, after the stack was successfully created:
    //
    //    aws cloudformation describe-stacks \
    //      --stack-name SetupStack \
    //      --query Stacks[0].Outputs \
    //      --output text
    //
    //    Bucketname    my-bucket-111111111111
    //
    // 2. When using the AWS CloudFormation console, after the stack was successfully
    //    created, by viewing the stack's Outputs tab.
    //
    // 3. When using the AWS CDK, after the stack was successfully created,
    //    the output displays: 
    //
    //    Outputs:
    //    SetupStack.Bucketname = my-bucket-111111111111
    //
    new CfnOutput(this, 'Bucket name', {
      value: myBucket.bucketName});
  }
}

// You must rename 'SetupStack' to a unique stack name.
// Otherwise, any existing stack in the caller's account with the
// name 'SetupStack' might produce unexpected results.
const app = new cdk.App();
new SetupStack(app, 'SetupStack');

