#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates:
//
//  * An Amazon DynamoDB table for tracking work items. The table contains an
//  `item_id` partition key.
//
//  * An AWS Identity and Access Management (IAM) role that grants permission to read
//  from and write to the table, and send email through Amazon Simple Email Service
//  (Amazon SES).
//
//  * An IAM user that has permission only to assume the previously created role.
//
// This stack is used by the python/cross_service/dynamodb_item_tracker example.

import 'source-map-support/register';
import { Construct } from "constructs";
import { App, Stack, StackProps, RemovalPolicy, CfnOutput } from 'aws-cdk-lib';
import { Table, AttributeType } from 'aws-cdk-lib/aws-dynamodb'
import { User, AccessKey, Effect, PolicyStatement, Role } from "aws-cdk-lib/aws-iam";

export class SetupStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const table: Table = new Table(this, 'doc-example-work-item-tracker', {
      tableName: 'doc-example-work-item-tracker',
      partitionKey: {
        name: 'item_id',
        type: AttributeType.STRING
      },
      removalPolicy: RemovalPolicy.DESTROY
    });

    const item_tracker_user: User = new User(this, 'doc-example-work-item-tracker-user', {
      userName: 'doc-example-work-item-tracker-user'
    });
    const item_tracker_user_keys = new AccessKey(this, 'doc-example-work-item-tracker-user-key', {
      user: item_tracker_user
    });

    const item_tracker_role: Role = new Role(this, 'doc-example-work-item-tracker-role', {
      roleName: 'doc-example-work-item-tracker-role',
      assumedBy: item_tracker_user,
    });
    table.grantReadWriteData(item_tracker_role)
    item_tracker_role.addToPolicy(new PolicyStatement({
      effect: Effect.ALLOW,
      resources: ['*'],
      actions: ['ses:SendEmail']
    }));

    new CfnOutput(this, 'TableName', {value: table.tableName})
    new CfnOutput(this, 'UserName', {value: item_tracker_user.userName})
    new CfnOutput(this, 'UserAccessKey', {value: item_tracker_user_keys.accessKeyId})
    new CfnOutput(this, 'UserAccessSecretKey', {value: item_tracker_user_keys.secretAccessKey.toString()})
    new CfnOutput(this, 'RoleArn', {value: item_tracker_role.roleArn})
  }
}

const stackName = 'doc-example-work-item-tracker-stack'

const app = new App();

new SetupStack(app, stackName);
