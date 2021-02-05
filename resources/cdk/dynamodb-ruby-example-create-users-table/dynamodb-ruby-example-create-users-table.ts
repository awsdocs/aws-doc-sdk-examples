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
// * dynamodb-ruby-example-create-users-table.rb
//
// You can run this app in several ways:
//
// 1. To run this app with the AWS Cloud Development Kit (AWS CDK), run the
//    following command:
// 
//    npm install && cdk synth && cdk deploy
//
//    The names of the generated AWS resources will display in the output.
//
// 2. To run this app with the AWS Command Line Interface (AWS CLI):
//
//    a. If a cdk.out folder exists in this directory, delete it.
//    b. Run the following command to create an AWS CloudFormation template:
//
//       npm install && cdk synth > dynamodb-ruby-example-create-users-table.yaml
//
//    c. Run the following command to create a stack
//       based on this AWS CloudFormation template. This stack
//       will create the specified AWS resources.
//
//       aws cloudformation create-stack --template-body file://dynamodb-ruby-example-create-users-table.yaml --stack-name DynamodbRubyExampleCreateUsersTableStack
//
//    d. To display the names of the generated resources, run the
//       following command:
//
//       aws cloudformation describe-stacks --stack-name DynamodbRubyExampleCreateUsersTableStack --query Stacks[0].Outputs --output text
//
//       Note that the generated resources might not be immediately available.
//       You can keep running this command until you see their names.
//
// 3. To run this app with the AWS CloudFormation console:
//
//    a. If a cdk.out folder exists in this directory, delete it.
//    b. Run the following command to create an AWS CloudFormation template:
//
//       npm install && cdk synth > dynamodb-ruby-example-create-users-table.yaml
//
//    c. Sign in to the AWS CloudFormation console, at:
//
//       https://console.aws.amazon.com/cloudformation
//
//       Choose Create stack, and then follow
//       the on-screen instructions to create a stack based on this 
//       AWS CloudFormation template. This stack will create the specified
//       AWS resources.
//
//       The names of the generated resources will display on the stack's
//       Outputs tab in the console after the stack's status displays as
//       CREATE_COMPLETE.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import * as dynamodb from '@aws-cdk/aws-dynamodb'; // npm install @aws-cdk/aws-dynamodb

export class DynamodbRubyExampleCreateUsersTableStack extends cdk.Stack {
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
new DynamodbRubyExampleCreateUsersTableStack(app, 'DynamodbRubyExampleCreateUsersTableStack');
