#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// This AWS CDK app creates the following resources:
//
// - An Amazon S3 bucket with public access to the bucket's objects
// - An IAM unauthenticated role based on an AWS identity
//   that has permission to create a DynamoDB table, invoke an Lambda
//   function,
//   and create a mobile analytics event.
// - An Amazon Cognito identity pool with the role attached to it.
//
// For instructions on how to run any of these apps, see
// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/README.md#Running-a-CDK-app

import "source-map-support/register";
import * as cdk from "@aws-cdk/core";
import { Bucket } from "@aws-cdk/aws-s3";
import * as cognito from "@aws-cdk/aws-cognito";
import * as iam from "@aws-cdk/aws-iam";
import { Effect, PolicyStatement } from "@aws-cdk/aws-iam";

export class SetupStackCreateTable extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    let mybucket: Bucket = new Bucket(this, "mybucket", {
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    });

    mybucket.grantPublicAccess("*", "s3:GetObject");

    const myIdentityPool = new cognito.CfnIdentityPool(
      this,
      "ExampleIdentityPool",
      {
        allowUnauthenticatedIdentities: true,
      }
    );

      const unauthenticatedRole = new iam.Role(this, 'CognitoDefaultUnauthenticatedRole', ({
          assumedBy: new iam.FederatedPrincipal('cognito-identity.amazonaws.com', {
              "StringEquals": {"cognito-identity.amazonaws.com:aud": myIdentityPool.ref},
              "ForAnyValue:StringLike": {"cognito-identity.amazonaws.com:amr": "unauthenticated"},
          }, "sts:AssumeRoleWithWebIdentity")
      }));
      unauthenticatedRole.assumeRolePolicy?.addStatements(
          new iam.PolicyStatement({
              effect: iam.Effect.ALLOW,
              principals: [new iam.ServicePrincipal('lambda.amazonaws.com')],
              actions: ['sts:AssumeRole'],
          }),
      )

    unauthenticatedRole.addToPolicy(
      new PolicyStatement({
        effect: Effect.ALLOW,
        actions: [
          "lambda:InvokeFunction",
          "mobileanalytics:PutEvents",
          "cognito-sync:*",
        ],
        resources: ["*"],
      })
    );
    unauthenticatedRole.addToPolicy(
      new PolicyStatement({
        effect: Effect.ALLOW,
        actions: ["dynamodb:CreateTable"],
        resources: ["*"],
      })
    );
    const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(
      this,
      "DefaultValid",
      {
        identityPoolId: myIdentityPool.ref,
        roles: {
          unauthenticated: unauthenticatedRole.roleArn,
        },
      }
    );
  }
}

const stackName = "SetupStackCreateTable";

const app = new cdk.App();

new SetupStackCreateTable(app, stackName);
