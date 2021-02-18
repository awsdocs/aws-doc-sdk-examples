#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// This AWS CDK app creates the following resources:
//
// - An AWS Identity and Access Management (IAM) unauthenticated role with full
//   access to Amazon Polly.
// - An Amazon Cognito identity pool with the IAM unauthenticated role attached to
//   it.
//
// For instructions on how to run any of these apps, see
// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/README.md#Running-a-CDK-app

import * as cdk from '@aws-cdk/core';
import * as cognito from '@aws-cdk/aws-cognito';
import * as iam from '@aws-cdk/aws-iam';
import {Effect, PolicyStatement} from '@aws-cdk/aws-iam';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const myIdentityPool = new cognito.CfnIdentityPool(this, "ExampleIdentityPool", {
      allowUnauthenticatedIdentities: true,
    });
    const unauthenticatedRole = new iam.Role(this, 'CognitoDefaultUnauthenticatedRole', {
      assumedBy: new iam.FederatedPrincipal('cognito-identity.amazonaws.com', {
        "StringEquals": { "cognito-identity.amazonaws.com:aud": myIdentityPool.ref },
        "ForAnyValue:StringLike": { "cognito-identity.amazonaws.com:amr": "unauthenticated" },
      }, "sts:AssumeRoleWithWebIdentity"),
    });
    unauthenticatedRole.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: [
        "mobileanalytics:PutEvents",
        "cognito-sync:*"
      ],
      resources: ["*"],
    }));
    unauthenticatedRole.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: [
        "polly:*"
      ],
      resources: ["*"],
    }));
    const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(this, 'DefaultValid', {
      identityPoolId: myIdentityPool.ref,
      roles: {
        'unauthenticated': unauthenticatedRole.roleArn
      }
    });
  }
}

const stackName = 'SetupStack'

const app = new cdk.App();

new SetupStack(app, stackName);
