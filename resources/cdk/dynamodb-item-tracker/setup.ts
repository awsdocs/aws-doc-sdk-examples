#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates:
//
//  * An Amazon DynamoDB table for tracking work items. The table contains an
//  `iditem` partition key.

import 'source-map-support/register';
import {Construct} from "constructs";
import {App, CfnOutput, RemovalPolicy, Stack, StackProps} from 'aws-cdk-lib';
import {AttributeType, Table} from 'aws-cdk-lib/aws-dynamodb'

export class SetupStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const table: Table = new Table(this, 'doc-example-work-item-tracker', {
      tableName: 'doc-example-work-item-tracker',
      partitionKey: {
        name: 'iditem',
        type: AttributeType.STRING
      },
      removalPolicy: RemovalPolicy.DESTROY
    });

    new CfnOutput(this, 'TableName', {value: table.tableName})
  }
}

const stackName = 'doc-example-work-item-tracker-stack'

const app = new App();

new SetupStack(app, stackName);
