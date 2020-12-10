#!/usr/bin/env node

// TODO: Customize this AWS CDK code for your specific AWS CDK solution.
// For more information, see the AWS CDK Developer Guide at
// https://docs.aws.amazon.com/cdk/latest/guide

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';

// Only include this import statement if your AWS CDK app needs to work
// with Amazon Simple Storage Service (Amazon S3) buckets. This import
// statement is included here only for this example.
import { Bucket } from '@aws-cdk/aws-s3';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // TODO: Add the code that defines your stack here.

    // For this example, here's how to create a bucket in Amazon S3.
    // Because S3 bucket names must be unique across AWS, this will generate
    // a random bucket name such as 'setupstack-mybucket15d133bf-q458tvg2mp9x'.
    let mybucket: Bucket = new Bucket(this, 'mybucket', {
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });
  }
}

// TODO: Change 'SetupStack' to a unique value across AWS CloudFormation
// across the caller's AWS account. Otherwise, attempting to deploying a
// stack with this stack name multiple times within the caller's account
// might fail.
const stackName = 'SetupStack'

const app = new cdk.App();

new SetupStack(app, stackName);
