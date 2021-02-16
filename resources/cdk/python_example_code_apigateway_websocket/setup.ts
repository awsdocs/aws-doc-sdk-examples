#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates the following resources:
//     * An Amazon DynamoDB table with a `connection_id` primary key.
//     * An AWS Identity and Access Management (IAM) role and policy that grants
//       AWS Lambda permission to access the DynamoDB table and have basic rights to
//       run functions.
//     * A Lambda function that runs on Python 3.7 and has an environment variable
//       that contains the DynamoDB table name. The function code is updated as part
//       of the example.
//
// This stack is used by the python/example_code/apigateway/websocket example.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {AttributeType, Table} from '@aws-cdk/aws-dynamodb';
import {Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal} from '@aws-cdk/aws-iam';
import {Code, Function, Runtime} from '@aws-cdk/aws-lambda';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const table: Table = new Table(this, 'doc-example-websocket-chat', {
      tableName: 'doc-example-websocket-chat',
      partitionKey: {
        name: 'connection_id',
        type: AttributeType.STRING
      },
      removalPolicy: cdk.RemovalPolicy.DESTROY
    })

    const role: Role = new Role(this, 'doc-example-apigateway-websocket-chat', {
      roleName: 'doc-example-apigateway-websocket-chat',
      assumedBy: new ServicePrincipal('lambda.amazonaws.com')
    })

    role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: [table.tableArn],
      actions: [
          'dynamodb:DeleteItem',
          'dynamodb:GetItem',
          'dynamodb:PutItem',
          'dynamodb:Scan'
      ]
    }))
    role.addManagedPolicy(
        ManagedPolicy.fromAwsManagedPolicyName(
            "service-role/AWSLambdaBasicExecutionRole"));

    const fn = new Function(this, 'doc-example-apigateway-websocket-connect', {
      runtime: Runtime.PYTHON_3_7,
      handler: 'lambda_chat.lambda_handler',
      code: Code.fromInline(
          "# This placeholder is replaced during code example deployment."),
      environment: {'table_name': table.tableName},
      role: role,

    });
  }
}

const stackName = 'python-example-code-apigateway-websocket-chat'

const app = new cdk.App();

new SetupStack(app, stackName);
