#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose: This AWS Cloud Development Kit (AWS CDK) app
// creates the following AWS resources:
//
// * A table in Amazon DynamoDB.
//
// You can run this app instead of running equivalent AWS SDK for Ruby
// code examples elsewhere in this repository, such as:
//
// * dynamodb_ruby_example_create_movies_table.rb
//
// For instructions on how to run any of these apps, see
// https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/README.md#Running-a-CDK-app

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import * as dynamodb from '@aws-cdk/aws-dynamodb'; // npm install @aws-cdk/aws-dynamodb

export class DynamodbRubyExampleCreateMoviesTableStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create the Amazon DynamoDB table.
    const table = new dynamodb.Table(this, 'table', {
      tableName: 'Movies',
      partitionKey: {
        name: 'year',
        type: dynamodb.AttributeType.NUMBER
      },
      sortKey: {
        name: 'title',
        type: dynamodb.AttributeType.STRING
      },
      readCapacity: 10,
      writeCapacity: 10,
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });

    // Output the name of the new table.
    new cdk.CfnOutput(this, 'TableName', {
      value: table.tableName
    });
  }
}

const app = new cdk.App();
new DynamodbRubyExampleCreateMoviesTableStack(app, 'DynamodbRubyExampleCreateMoviesTableStack');
