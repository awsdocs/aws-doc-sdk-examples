#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates the following resources:
// * An Amazon S3 bucket that grants Amazon Textract read-write permission.
// * An Amazon SNS topic.
// * An IAM role that can be assumed by Textract and grants permission to publish to
// the topic.
// * An Amazon SQS queue that is subscribed to receive messages from the topic.
//
// Outputs:
//   * The name of the Amazon S3 bucket.
//   * The Amazon Resource Name (ARN) of the Amazon SNS topic.
//   * The ARN of the IAM role.
//   * The URL of the Amazon SQS queue.
//
// This stack is used by:
//   * python/example_code/textract/textract_demo_launcher.py.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {CfnOutput} from '@aws-cdk/core';
import {Bucket} from '@aws-cdk/aws-s3';
import {Role, ServicePrincipal} from '@aws-cdk/aws-iam';
import {Topic} from '@aws-cdk/aws-sns';
import {SqsSubscription} from "@aws-cdk/aws-sns-subscriptions";
import {Queue} from '@aws-cdk/aws-sqs';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    let textract = new ServicePrincipal('textract.amazonaws.com');

    let bucket = new Bucket(this, 'textract-demo-bucket', {
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });
    bucket.grantReadWrite(textract);

    let queue = new Queue(this, 'textract-demo-queue', {});

    let topic = new Topic(this, 'textract-demo-topic', {});
    topic.addSubscription(new SqsSubscription(queue));

    let role = new Role(this, 'textract-demo-role', {
      assumedBy: textract
    });
    topic.grantPublish(role);

    new CfnOutput(this, 'BucketName', {value: bucket.bucketName});
    new CfnOutput(this, 'TopicArn', {value: topic.topicArn});
    new CfnOutput(this, 'RoleArn', {value: role.roleArn});
    new CfnOutput(this, 'QueueUrl', {value: queue.queueUrl});
  }
}

const stackName = 'textract-example-s3-sns-sqs'

const app = new cdk.App();

new SetupStack(app, stackName);
