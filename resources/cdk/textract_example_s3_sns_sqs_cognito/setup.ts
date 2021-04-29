#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates the following resources:
// * An Amazon S3 bucket that grants Amazon Textract read-write permission.
// * An Amazon SNS topic.
// * An IAM role that can be assumed by Amazon Textract and grants permission to
// publish to the topic.
// * An Amazon SQS queue that is subscribed to receive messages from the topic.
// * An Amazon Cognito user pool, identity pool, and authenticated user role that
// grants authenticated users permission to access Amazon Textract, the Amazon SQS
// queue, and the Amazon S3 bucket.
//
// Outputs:
//   * The AWS Region where the resources are deployed.
//   * The name of the Amazon S3 bucket.
//   * The Amazon Resource Name (ARN) of the Amazon SNS topic.
//   * The ARN of the IAM role.
//   * The URL of the Amazon SQS queue.
//   * The ID of the Amazon Cognito user pool identity provider.
//   * The ID of the Amazon Cognito user pool.
//   * The ID of the Amazon Cognito identity pool.
//   * The URL of the hosted UI where users can sign in to Amazon Cognito.
//
// This stack is used by:
//   * javascriptv3/example_code/cross-services/textract-react

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {CfnOutput} from '@aws-cdk/core';
import {Bucket, HttpMethods} from '@aws-cdk/aws-s3';
import {Role, ServicePrincipal, FederatedPrincipal, PolicyStatement, ManagedPolicy, Effect, Policy}
  from '@aws-cdk/aws-iam';
import {Topic} from '@aws-cdk/aws-sns';
import {SqsSubscription} from "@aws-cdk/aws-sns-subscriptions";
import {Queue} from '@aws-cdk/aws-sqs';
import {UserPool, UserPoolClient, CfnIdentityPool, CfnIdentityPoolRoleAttachment}
  from "@aws-cdk/aws-cognito";

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    let textract = new ServicePrincipal('textract.amazonaws.com');

    let bucket = new Bucket(this, 'textract-cognito-demo-bucket', {
      cors: [{
        allowedHeaders: ["*"],
        allowedMethods: [HttpMethods.GET],
        allowedOrigins: ["*"],
        exposedHeaders: ["ETag", "x-amz-meta-custom-header"]
      }],
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });
    bucket.grantReadWrite(textract);

    let queue = new Queue(this, 'textract-cognito-demo-queue', {});

    let topic = new Topic(this, 'textract-cognito-demo-topic', {});
    topic.addSubscription(new SqsSubscription(queue));

    let textractRole = new Role(this, 'textract-cognito-demo-textract-role', {
      assumedBy: textract
    });
    topic.grantPublish(textractRole);

    const exampleUserPoolName = `textract-cognito-demo`;
    const exampleUserPool = new UserPool(this, 'textract-cognito-demo-id', {
      userPoolName: exampleUserPoolName,
      selfSignUpEnabled: true,
      autoVerify: {
        email: true
      },
      standardAttributes: {
        email: {
          required: true,
          mutable: false,
        }
      }
    });

    const exampleUserPoolClientName = `textract-cognito-demo-client`;
    const redirectUri = "http://localhost:3000";
    const exampleUserPoolClient = exampleUserPool.addClient('textract-cognito-demo-client-id', {
      userPoolClientName: exampleUserPoolClientName,
      oAuth: {callbackUrls: [redirectUri]}
    });

    const domainPrefix = 'textract-demo';
    exampleUserPool.addDomain('textract-cognito-demo-domain', {
      cognitoDomain: {
        domainPrefix: domainPrefix
      }
    });

    const exampleIdentityPool = new CfnIdentityPool(this, "textract-cognito-demo-idpool", {
      allowUnauthenticatedIdentities: false,
      cognitoIdentityProviders: [{
        clientId: exampleUserPoolClient.userPoolClientId,
        providerName: exampleUserPool.userPoolProviderName,
      }]
    });
    const authenticatedRole = new Role(this, 'textract-cognito-demo-authenticated-role', {
      assumedBy: new FederatedPrincipal('cognito-identity.amazonaws.com', {
        "StringEquals": {"cognito-identity.amazonaws.com:aud": exampleIdentityPool.ref},
        "ForAnyValue:StringLike": {"cognito-identity.amazonaws.com:amr": "authenticated"},
      }, "sts:AssumeRoleWithWebIdentity"),
    });
    authenticatedRole.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName('AmazonTextractFullAccess'));
    authenticatedRole.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: [
        "mobileanalytics:PutEvents",
        "cognito-sync:*",
        "cognito-identity:*"
      ],
      resources: ["*"]
    }));
    authenticatedRole.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: ["sqs:ReceiveMessage", "sqs:DeleteMessage"],
      resources: [queue.queueArn]
    }));
    authenticatedRole.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: ['s3:GetObject'],
      resources: [`${bucket.bucketArn}/*`]
    }));
    const defaultPolicy = new CfnIdentityPoolRoleAttachment(this, 'DefaultValid', {
      identityPoolId: exampleIdentityPool.ref,
      roles: {
        'authenticated': authenticatedRole.roleArn
      }
    });

    new CfnOutput(this, 'DeployRegion', {value: this.region});
    new CfnOutput(this, 'DefaultBucketName', {value: bucket.bucketName});
    new CfnOutput(this, 'SNSTopicArn', {value: topic.topicArn});
    new CfnOutput(this, 'RoleArn', {value: textractRole.roleArn});
    new CfnOutput(this, 'QueueUrl', {value: queue.queueUrl});
    new CfnOutput(this, 'CognitoId', {value: `cognito-idp.${this.region}.amazonaws.com/${exampleUserPool.userPoolId}`});
    new CfnOutput(this, 'CognitoUserPoolId', {value: exampleUserPool.userPoolId});
    new CfnOutput(this, 'CognitoIdentityPoolId', {value: exampleIdentityPool.ref});
    new CfnOutput(this, 'LoginUrl', {value:
      `https://${domainPrefix}.auth.${this.region}.amazoncognito.com/login?` +
      `client_id=${exampleUserPoolClient.userPoolClientId}` +
      `&response_type=token` +
      `&scope=aws.cognito.signin.user.admin+email+openid+phone+profile` +
      `&redirect_uri=${redirectUri}`});
  }
}

const stackName = 'textract-example-s3-sns-sqs-cognito'

const app = new cdk.App();

new SetupStack(app, stackName);
