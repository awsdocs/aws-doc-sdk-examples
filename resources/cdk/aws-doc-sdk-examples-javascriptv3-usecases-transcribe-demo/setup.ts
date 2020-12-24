#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// For more information, see the AWS CDK Developer Guide at
// https://docs.aws.amazon.com/cdk/latest/guide

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import * as cdk from '@aws-cdk/core';
import * as cognito from '@aws-cdk/aws-cognito';
import * as iam from '@aws-cdk/aws-iam';
import {Effect, PolicyStatement} from '@aws-cdk/aws-iam';
import * as s3 from '@aws-cdk/aws-s3';
import {Bucket, BucketAccessControl} from '@aws-cdk/aws-s3';

export class  SetupStack extends cdk.Stack {
    constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const transcriptionBucket = new Bucket(this, 'transciptions', {
            removalPolicy: cdk.RemovalPolicy.DESTROY,
            publicReadAccess: true,
            accessControl: BucketAccessControl.PUBLIC_READ_WRITE
        })

        transcriptionBucket.addToResourcePolicy(new iam.PolicyStatement({
            effect: Effect.ALLOW,
            actions: ["*"],
            resources: [transcriptionBucket.arnForObjects('*')],
            principals: [new iam.AccountRootPrincipal()],
        }));
        transcriptionBucket.grantPublicAccess('*', 's3:GetObject');
        transcriptionBucket.grantPublicAccess('*', 's3:PutObject');

        const cfnBucket = transcriptionBucket.node.findChild('Resource') as s3.CfnBucket
        cfnBucket.addPropertyOverride('CorsConfiguration', {
            CorsRules: [
                {
                    "AllowedHeaders": [
                        "*"
                    ],
                    "AllowedMethods": [
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE"
                    ],
                    "AllowedOrigins": [
                        "*"
                    ],
                }
            ]
        });

        const appBucket = new s3.Bucket(this, 'appbucket',{
            removalPolicy: cdk.RemovalPolicy.DESTROY,
            websiteIndexDocument: 'index.html',
            websiteErrorDocument: 'index.html'
        });
        appBucket.grantPublicAccess('*', 's3:GetObject');
        appBucket.grantPublicAccess('*', 's3:PutObject');

        const randomUserPoolName = `ExampleUserPoolName-${Math.ceil(Math.random() * 10 ** 10)}`;

        const myUserPool = new cognito.UserPool(this, 'exampleuserpoolid', {
            userPoolName: randomUserPoolName,
            selfSignUpEnabled: true,
            autoVerify: {
                email: true
            },
            userVerification: {
                emailSubject: 'Verify your email for our awesome app!',
                emailBody: 'Hello, Thanks for signing up to our awesome app! Your verification code is {####}',
                smsMessage: 'Hello, Thanks for signing up to our awesome app! Your verification code is {####}',
            },
            standardAttributes: {
                email: {
                    required: true,
                    mutable: false,
                }
            }
        });

        const randomUserPoolClientName = `ExampleUserPoolName-${Math.ceil(Math.random() * 10 ** 10)}`;

        const userPoolClient = new cognito.UserPoolClient(this, 'ExampleUserPoolClient', {
            userPool: myUserPool,
            userPoolClientName: randomUserPoolClientName,
            oAuth: {
                callbackUrls: [appBucket.urlForObject('index.html')]
            }
        });

        const randomDomain = `exampleuserpoolname-${Math.ceil(Math.random() * 10 ** 10)}`;

        myUserPool.addDomain('CognitoDomain', {
            cognitoDomain: {
                domainPrefix: randomDomain
            }
        });

        const myIdentityPool = new cognito.CfnIdentityPool(this, "ExampleIdentityPool", {
            allowUnauthenticatedIdentities: false,
            cognitoIdentityProviders: [{
                clientId: userPoolClient.userPoolClientId,
                providerName: myUserPool.userPoolProviderName,
            }]
        });
        const authenticatedRole = new iam.Role(this, 'ExampleCognitoDefaultAuthenticatedRole', {
            assumedBy: new iam.FederatedPrincipal('cognito-identity.amazonaws.com', {
                "StringEquals": {"cognito-identity.amazonaws.com:aud": myIdentityPool.ref},
                "ForAnyValue:StringLike": {"cognito-identity.amazonaws.com:amr": "authenticated"},
            }, "sts:AssumeRoleWithWebIdentity"),
        });
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
            actions: ["sns:*"],
            resources: ["*"]
        }));
        authenticatedRole.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ["transcribe:StartTranscriptionJob",
                "transcribe:GetTranscriptionJob"],
            resources: ["*"]
        }));
        const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(this, 'DefaultValid', {
            identityPoolId: myIdentityPool.ref,
            roles: {
                'authenticated': authenticatedRole.roleArn
            }
        });
    }
};

// Change 'SetupStack' to a unique value across AWS CloudFormation
// across the caller's AWS account. Otherwise, attempting to deploying a
// stack with this stack name multiple times within the caller's account
// might fail. If you are modifying transcript-app-setup.yaml, use
// 'AwsDocSdkExamplesJavascriptv3ScenariosTransciptionAppStack'.

const stackName = 'SetupStack'

const app = new cdk.App();

new SetupStack(app, stackName);
