#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// * A table in Amazon DynamoDB.
//
// For instructions on how to run any of these apps, see
// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/README.md#Running-a-CDK-app

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import * as dynamodb from '@aws-cdk/aws-dynamodb'; // npm install @aws-cdk/aws-dynamodb

export class DynamodbRubyExampleCreateUserTableStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create the Amazon DynamoDB table.
    const table = new dynamodb.Table(this, 'table', {
      tableName: 'Users',
      partitionKey: {
        name: 'ID',
        type: dynamodb.AttributeType.NUMBER
      },
      readCapacity: 5,
      writeCapacity: 10,
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });

    // Add two global secondary indexes to the table.
    table.addGlobalSecondaryIndex({
      indexName: 'LastNameFirstNameIndex',
      partitionKey: {
        name: 'FirstName',
        type: dynamodb.AttributeType.STRING
      },
      sortKey: {
        name: 'LastName',
        type: dynamodb.AttributeType.STRING
      },
      projectionType: dynamodb.ProjectionType.ALL,
      readCapacity: 5,
      writeCapacity: 10
    });

    table.addGlobalSecondaryIndex({
      indexName: 'AirMileageIndex',
      partitionKey: {
        name: 'AirMiles',
        type: dynamodb.AttributeType.NUMBER
      },
      projectionType: dynamodb.ProjectionType.ALL,
      readCapacity: 5,
      writeCapacity: 10
    });

    // Output the name of the new table.
    new cdk.CfnOutput(this, 'TableName', {
      value: table.tableName
    });
  }
}

const app = new cdk.App();
new DynamodbRubyExampleCreateUserTableStack(app, 'DynamodbRubyExampleCreateUserTableStack');
