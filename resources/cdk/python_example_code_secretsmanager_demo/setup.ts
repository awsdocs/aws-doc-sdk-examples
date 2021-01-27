#!/usr/bin/env node

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates the following resources:
//   * An Amazon Aurora serverless cluster and accompanying infrastructure.
//
// Outputs:
//   * The Amazon Resource Name (ARN) of the Amazon Aurora cluster.
//   * The ID of the AWS Secrets Manager secret that contains the credentials
//     needed to access the cluster.
//
// This stack is used by the python/example_code/secretsmanager/secretsmanager_basics example.

import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {DatabaseClusterEngine, ServerlessCluster} from '@aws-cdk/aws-rds'
import {Vpc} from "@aws-cdk/aws-ec2";
import {CfnOutput} from "@aws-cdk/core";

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vpc = new Vpc(this, 'doc-example-secretsmanager-vpc')

    const cluster = new ServerlessCluster(this, 'doc-example-database-for-secretsmanager', {
        engine: DatabaseClusterEngine.AURORA_MYSQL, vpc, enableDataApi: true
    })

    new CfnOutput(this, 'ClusterArn', {value: cluster.clusterArn})
    new CfnOutput(this, 'SecretId', {value: cluster.secret!.secretName})
  }
}

const stackName = 'python-example-code-secretsmanager-demo'

const app = new cdk.App();

new SetupStack(app, stackName);
