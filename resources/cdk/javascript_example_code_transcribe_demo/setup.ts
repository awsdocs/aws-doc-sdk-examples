#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// This AWS CDK app creates the following resources:
//
// - An Amazon Cognito identity pool with an authenticated user role.
// - An IAM policy with permissions for the Amazon S3 and Amazon Transcribe is
//   attached to the authenticated user role.
// - An Amazon Cognito user pool that enables users to sign up and sign in to the
//   app.
// - An Amazon S3 bucket to host the application files.
// - An Amazon S3 bucket to to store the transcriptions.
//
// For instructions on how to run any of these apps, see
// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/README.md#Running-a-CDK-app

import * as cdk from "@aws-cdk/core";
import * as cognito from "@aws-cdk/aws-cognito";
import * as iam from "@aws-cdk/aws-iam";
import { Effect, PolicyStatement } from "@aws-cdk/aws-iam";
import * as s3 from "@aws-cdk/aws-s3";
import {
  Bucket,
  BucketAccessControl,
  BlockPublicAccess,
  BlockPublicAccessOptions,
} from "@aws-cdk/aws-s3";

export class SetupStackTranscription extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const transcriptionBucket = new Bucket(this, "transcriptions", {
      enforceSSL: true,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      publicReadAccess: true,
      accessControl: BucketAccessControl.PUBLIC_READ,
    });

    transcriptionBucket.addToResourcePolicy(
      new iam.PolicyStatement({
        effect: Effect.ALLOW,
        actions: ["*"],
        resources: [transcriptionBucket.arnForObjects("*")],
        principals: [new iam.AccountRootPrincipal()],
      })
    );
    transcriptionBucket.grantPublicAccess("*", "s3:GetObject");
    transcriptionBucket.grantPublicAccess("*", "s3:PutObject");
    transcriptionBucket.grantPublicAccess("*", "s3:DeleteObject");

    const cfnBucket = transcriptionBucket.node.findChild(
      "Resource"
    ) as s3.CfnBucket;
    cfnBucket.addPropertyOverride("CorsConfiguration", {
      CorsRules: [
        {
          AllowedHeaders: ["*"],
          AllowedMethods: ["GET", "POST", "PUT", "DELETE"],
          AllowedOrigins: ["*"],
        },
      ],
    });

    const appBucket = new s3.Bucket(this, "appbucket", {
      enforceSSL: true,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      websiteIndexDocument: "index.html",
      websiteErrorDocument: "index.html",
      blockPublicAccess: {
        blockPublicAcls: true,
        blockPublicPolicy: false,
        ignorePublicAcls: true,
        restrictPublicBuckets: false,
      },
    });
    appBucket.grantPublicAccess("*", "s3:GetObject");
    appBucket.grantPublicAccess("*", "s3:PutObject");

    const randomUserPoolName = `ExampleUserPoolName-${Math.ceil(
      Math.random() * 10 ** 10
    )}`;

    const myUserPool = new cognito.UserPool(this, "exampleuserpoolid", {
      userPoolName: randomUserPoolName,
      selfSignUpEnabled: true,
      autoVerify: {
        email: true,
      },
      userVerification: {
        emailSubject: "Verify your email for our awesome app!",
        emailBody:
          "Hello, Thanks for signing up to our awesome app! Your verification code is {####}",
        smsMessage:
          "Hello, Thanks for signing up to our awesome app! Your verification code is {####}",
      },
      standardAttributes: {
        email: {
          required: true,
          mutable: false,
        },
      },
    });

    const randomUserPoolClientName = `ExampleUserPoolName-${Math.ceil(
      Math.random() * 10 ** 10
    )}`;

    const userPoolClient = new cognito.UserPoolClient(
      this,
      "ExampleUserPoolClient",
      {
        userPool: myUserPool,
        userPoolClientName: randomUserPoolClientName,
        oAuth: {
          callbackUrls: [appBucket.urlForObject("index.html")],
        },
      }
    );

    const randomDomain = `exampleuserpoolname-${Math.ceil(
      Math.random() * 10 ** 10
    )}`;

    myUserPool.addDomain("CognitoDomain", {
      cognitoDomain: {
        domainPrefix: randomDomain,
      },
    });

    const myIdentityPool = new cognito.CfnIdentityPool(
      this,
      "ExampleIdentityPool",
      {
        allowUnauthenticatedIdentities: false,
        cognitoIdentityProviders: [
          {
            clientId: userPoolClient.userPoolClientId,
            providerName: myUserPool.userPoolProviderName,
          },
        ],
      }
    );
    const authenticatedRole = new iam.Role(
      this,
      "ExampleCognitoDefaultAuthenticatedRole",
      {
        assumedBy: new iam.FederatedPrincipal(
          "cognito-identity.amazonaws.com",
          {
            StringEquals: {
              "cognito-identity.amazonaws.com:aud": myIdentityPool.ref,
            },
            "ForAnyValue:StringLike": {
              "cognito-identity.amazonaws.com:amr": "authenticated",
            },
          },
          "sts:AssumeRoleWithWebIdentity"
        ),
      }
    );
    authenticatedRole.addToPolicy(
      new PolicyStatement({
        effect: Effect.ALLOW,
        actions: [
          "mobileanalytics:PutEvents",
          "cognito-sync:*",
          "cognito-identity:*",
        ],
        resources: ["*"],
      })
    );

    authenticatedRole.addToPolicy(
      new PolicyStatement({
        effect: Effect.ALLOW,
        actions: ["sns:*"],
        resources: ["*"],
      })
    );
    authenticatedRole.addToPolicy(
      new PolicyStatement({
        effect: Effect.ALLOW,
        actions: [
          "transcribe:StartTranscriptionJob",
          "transcribe:GetTranscriptionJob",
        ],
        resources: ["*"],
      })
    );
    const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(
      this,
      "DefaultValid",
      {
        identityPoolId: myIdentityPool.ref,
        roles: {
          authenticated: authenticatedRole.roleArn,
        },
      }
    );
  }
}

const stackName = "SetupStack";

const app = new cdk.App();

new SetupStackTranscription(app, stackName);
