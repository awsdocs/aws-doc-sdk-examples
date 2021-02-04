#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// * A bucket in Amazon Simple Storage Service (Amazon S3).
//
// You can run this app instead of running equivalent AWS SDK for Ruby
// code examples elsewhere in this repository, such as:
//
// * create_bucket_snippet.rb
// * s3_ruby_create_bucket.rb
// * s3-ruby-example-create-bucket.rb
//
// You can run this app in several ways:
//
// 1. To run this app with the AWS Cloud Development Kit (AWS CDK), run the
//    following command:
// 
//    npm install && cdk synth && cdk deploy
//
//    The names of the generated AWS resources will display in the output.
//
// 2. To run this app with the AWS Command Line Interface (AWS CLI):
//
//    a. If a cdk.out folder exists in this directory, delete it.
//    b. Run the following command to create an AWS CloudFormation template:
//
//       npm install && cdk synth > s3-ruby-example-create-bucket.yaml
//
//    c. Run the following command to create a stack
//       based on this AWS CloudFormation template. This stack
//       will create the specified AWS resources.
//
//       aws cloudformation create-stack --template-body file://s3-ruby-example-create-bucket.yaml --stack-name S3RubyExampleCreateBucketStack
//
//    d. To display the names of the generated resources, run the
//       following command:
//
//       aws cloudformation describe-stacks --stack-name S3RubyExampleCreateBucketStack --query Stacks[0].Outputs --output text
//
//       Note that the generated resources might not be immediately available.
//       You can keep running this command until you see their names.
//
// 3. To run this app with the AWS CloudFormation console:
//
//    a. If a cdk.out folder exists in this directory, delete it.
//    b. Run the following command to create an AWS CloudFormation template:
//
//       npm install && cdk synth > s3-ruby-example-create-bucket.yaml
//
//    c. Sign in to the AWS CloudFormation console, at:
//
//       https://console.aws.amazon.com/cloudformation
//
//       Choose Create stack, and then follow
//       the on-screen instructions to create a stack based on this 
//       AWS CloudFormation template. This stack will create the specified
//       AWS resources.
//
//       The names of the generated resources will display on the stack's
//       Outputs tab in the console after the stack's status displays as
//       CREATE_COMPLETE.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import * as s3 from '@aws-cdk/aws-s3'; // npm install @aws-cdk/aws-s3

export class S3RubyExampleCreateBucketStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);
  
    // Create the Amazon S3 bucket.
    let myBucket = new s3.Bucket(this, 'bucket', {
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });

    // Output the name of the new bucket.
    new cdk.CfnOutput(this, 'BucketName', {
      value: myBucket.bucketName});
  }
}

const app = new cdk.App();
new S3RubyExampleCreateBucketStack(app, 'S3RubyExampleCreateBucketStack');
