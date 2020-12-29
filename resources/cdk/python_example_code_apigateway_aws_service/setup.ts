#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines a CloudFormation stack that creates an Amazon DynamoDB table with a
// `username` partition key and an AWS Identity and Access Management (IAM) role
// that lets Amazon API Gateway read from and write to the table.
//
// This stack is used by the python/example_code/apigateway/aws_service example.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {AttributeType, Table} from '@aws-cdk/aws-dynamodb';
import {Effect, PolicyStatement, Role, ServicePrincipal} from '@aws-cdk/aws-iam';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const table: Table = new Table(this, 'doc-example-profiles', {
      tableName: 'doc-example-profiles',
      partitionKey: {
        name: 'username',
        type: AttributeType.STRING
      },
      removalPolicy: cdk.RemovalPolicy.DESTROY
    })

    const role: Role = new Role(this, 'doc-example-apigateway-dynamodb-profiles', {
      roleName: 'doc-example-apigateway-dynamodb-profiles',
      assumedBy: new ServicePrincipal('apigateway.amazonaws.com')
    })

    role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: [table.tableArn],
      actions: [
          'dynamodb:GetItem',
          'dynamodb:PutItem',
          'dynamodb:Scan'
      ]
    }))
  }
}

const stackName = 'python-example-code-apigateway-dynamodb-profiles'

const app = new cdk.App();

new SetupStack(app, stackName);
