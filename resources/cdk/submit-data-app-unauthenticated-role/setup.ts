#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// TODO: Customize this AWS Cloud Development Kit (AWS CDK) code for your
// specific AWS CDK solution. For more information, see the
// AWS CDK Developer Guide at
// https://docs.aws.amazon.com/cdk/latest/guide

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {CfnOutput} from '@aws-cdk/core';
import * as cognito from "@aws-cdk/aws-cognito";
import * as iam from "@aws-cdk/aws-iam";
import * as dynamodb from '@aws-cdk/aws-dynamodb';

import {Effect, PolicyStatement} from '@aws-cdk/aws-iam';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);


    const table = new dynamodb.Table(this, 'table', {
          tableName: 'Items',
          partitionKey: {
              name: 'id',
              type: dynamodb.AttributeType.NUMBER
          },
          readCapacity: 10,
          writeCapacity: 10,
          removalPolicy: cdk.RemovalPolicy.DESTROY
      });
      // Add global secondary index to the table.
      table.addGlobalSecondaryIndex({
          indexName: 'idIndex',
          partitionKey: {
              name: 'id',
              type: dynamodb.AttributeType.NUMBER
          },
          projectionType: dynamodb.ProjectionType.ALL,
          readCapacity: 5,
          writeCapacity: 10
      });

    const myIdentityPool = new cognito.CfnIdentityPool(
        this,
        "ExampleIdentityPool",
        {
          allowUnauthenticatedIdentities: true,
        }
    );

    const unauthenticatedRole = new iam.Role(
        this,
        "CognitoDefaultUnauthenticatedRole",
        {
          assumedBy: new iam.FederatedPrincipal(
              "cognito-identity.amazonaws.com",
              {
                StringEquals: {
                  "cognito-identity.amazonaws.com:aud": myIdentityPool.ref,
                },
                "ForAnyValue:StringLike": {
                  "cognito-identity.amazonaws.com:amr": "unauthenticated",
                },
              },
              "sts:AssumeRoleWithWebIdentity"
          ),
        }
    );
    unauthenticatedRole.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["mobileanalytics:PutEvents", "cognito-sync:*"],
          resources: ["*"],
        })
    );
    unauthenticatedRole.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["sns:Publish"],
          resources: ["*"],
        })
    );

    unauthenticatedRole.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["dynamodb:PutItem"],
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
    new CfnOutput(this, 'TableName', {value: table.tableName});
    new CfnOutput(this, "Identity pool id", { value: myIdentityPool.ref });
  }
};

// You must rename 'SetupStack3' to a unique stack name.
// Otherwise, any stack in the caller's account with the
// name 'SetupStack3' might produce unexpected results.
const app = new cdk.App();
new SetupStack(app, 'SetupStack');
