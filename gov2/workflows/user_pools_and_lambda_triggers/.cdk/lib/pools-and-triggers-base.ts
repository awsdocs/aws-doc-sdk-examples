// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import * as cdk from 'aws-cdk-lib';
import {CfnOutput, RemovalPolicy} from 'aws-cdk-lib';
import {Construct} from 'constructs';
import * as dynamo from 'aws-cdk-lib/aws-dynamodb';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import * as iam from 'aws-cdk-lib/aws-iam';

export class PoolsAndTriggersBase extends Construct {
  readonly tableName: string;
  readonly userPoolId: string;
  readonly userPoolArn: string;
  readonly userPoolClientId: string;
  readonly lambdaRole: iam.Role;

  constructor(scope: Construct, id: string) {
    super(scope, id);
    this.tableName = "doc-example-custom-users"

    const table = new dynamo.Table(this, "doc-example-custom-users", {
      tableName: this.tableName,
      partitionKey: {name: "UserEmail", type: dynamo.AttributeType.STRING},
      removalPolicy: RemovalPolicy.DESTROY
    })

    const pool = new cognito.UserPool(this, "doc-example-pools-and-triggers", {
      userPoolName: "doc-examples-pools-and-triggers",
      selfSignUpEnabled: true,
      standardAttributes: {email: {mutable: false, required: true}},
      accountRecovery: cognito.AccountRecovery.EMAIL_ONLY,
      removalPolicy: RemovalPolicy.DESTROY,
      deletionProtection: false
    })
    const poolClient = new cognito.UserPoolClient(this, "doc-example-pools-and-triggers-client", {
      userPool: pool,
      userPoolClientName: "doc-example-pools-and-triggers-client",
      authFlows: {userPassword: true}
    })
    this.userPoolId = pool.userPoolId
    this.userPoolArn = pool.userPoolArn
    this.userPoolClientId = poolClient.userPoolClientId

    this.lambdaRole = new iam.Role(this, "doc-example-pools-and-triggers-lambda-role", {
      roleName: "doc-example-pools-and-triggers-lambda-role",
      assumedBy: new iam.ServicePrincipal("lambda.amazonaws.com"),
      managedPolicies: [iam.ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")]
    })
    table.grantReadWriteData(this.lambdaRole)

  }

  outputs(scope: Construct) {
    new CfnOutput(scope, "TableName", {value: this.tableName})
    new CfnOutput(scope, "UserPoolId", {value: this.userPoolId})
    new CfnOutput(scope, "UserPoolArn", {value: this.userPoolArn})
    new CfnOutput(scope, "UserPoolClientId", {value: this.userPoolClientId})
  }
}
