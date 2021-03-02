#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates an Amazon DynamoDB table with a
// `username` partition key and an AWS Identity and Access Management (IAM) role
// that lets Amazon API Gateway read from and write to the table.
//
// This stack is used by the python/example_code/apigateway/aws_service example.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {AttributeType, Table} from '@aws-cdk/aws-dynamodb';
import {Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal} from '@aws-cdk/aws-iam';
import {Code, Function, Runtime} from '@aws-cdk/aws-lambda'
import {Queue} from '@aws-cdk/aws-sqs'
import {CfnOutput} from "@aws-cdk/core";

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const table: Table = new Table(this, 'doc-example-stepfunctions-messages', {
      tableName: 'doc-example-stepfunctions-messages',
      partitionKey: {
        name: 'user_name',
        type: AttributeType.STRING
      },
      sortKey: {
        name: 'message_id',
        type: AttributeType.STRING
      },
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });

    const lambda_role: Role = new Role(this, 'doc-example-stepfunctions-lambda-messages-role', {
      roleName: 'doc-example-stepfunctions-lambda-messages-role',
      assumedBy: new ServicePrincipal('lambda.amazonaws.com')
    });
    lambda_role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: [table.tableArn],
      actions: ['dynamodb:Scan']
    }));
    lambda_role.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName(
        "service-role/AWSLambdaBasicExecutionRole"));


    const fn: Function = new Function(this, 'doc-example-stepfunctions-scan-messages', {
      runtime: Runtime.PYTHON_3_7,
      handler: 'index.lambda_handler',
      code: Code.fromInline(
        "import boto3\n" +
        "from boto3.dynamodb.conditions import Attr\n" +
        "def lambda_handler(event, context):\n" +
        "    return boto3.resource('dynamodb').Table('" + table.tableName + "').scan(" +
               "FilterExpression=Attr('sent').eq(False))['Items']"),
      role: lambda_role
    });

    const queue: Queue = new Queue(this, 'doc-example-stepfunctions-queue', {
      queueName: 'doc-example-stepfunctions-queue'
    })

    const step_role: Role = new Role(this, 'doc-example-stepfunctions-messages-role', {
      roleName: 'doc-example-stepfunctions-messages-role',
      assumedBy: new ServicePrincipal('states.amazonaws.com'),
    });

    step_role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: [table.tableArn],
      actions: [
        'dynamodb:UpdateItem'
      ]
    }));
    step_role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: [fn.functionArn],
      actions: ['lambda:InvokeFunction']
    }));
    step_role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: [queue.queueArn],
      actions: ['sqs:SendMessage']
    }))

    new CfnOutput(this, 'MessageTableName', {value: table.tableName})
    new CfnOutput(this, 'SendQueueUrl', {value: queue.queueUrl})
    new CfnOutput(this, 'ScanFunctionArn', {value: fn.functionArn})
    new CfnOutput(this, 'StepRoleArn', {value: step_role.roleArn})
  }
}

const stackName = 'doc-example-stepfunctions-messages-stack'

const app = new cdk.App();

new SetupStack(app, stackName);
