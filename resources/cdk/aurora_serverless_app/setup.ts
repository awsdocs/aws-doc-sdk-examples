// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Defines an AWS CloudFormation stack that creates AWS resources for Amazon Aurora
 * sample applications.
 *
 * * An AWS Secrets Manager secret that contains administrator credentials in a format
 *   that can be used by an Aurora MySQL database.
 * * An Aurora MySQL database configured to use the credentials from the secret.
  */

import 'source-map-support/register';
import {Construct} from "constructs";
import {App, CfnOutput, Duration, Stack, StackProps} from 'aws-cdk-lib';
import {Secret} from 'aws-cdk-lib/aws-secretsmanager';
import {Credentials, DatabaseClusterEngine, ServerlessCluster} from "aws-cdk-lib/aws-rds";

export class SetupStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const username = 'docexampleadmin'
    const secret: Secret = new Secret(this, 'doc-example-aurora-app-secret', {
        generateSecretString: {
            secretStringTemplate: JSON.stringify({
              username: username
            }),
            generateStringKey: 'password',
            excludePunctuation: true,
            includeSpace: false,
        }
    });

    const dbname = 'auroraappdb'
    const cluster: ServerlessCluster = new ServerlessCluster(this, 'doc-example-aurora-app-cluster', {
      engine: DatabaseClusterEngine.AURORA_MYSQL,
      defaultDatabaseName: dbname,
      enableDataApi: true,
      scaling: {autoPause: Duration.minutes(0)},
      credentials: Credentials.fromSecret(secret, username)
    })

    // Create outputs from the stack. These values are required by Amazon Relational
    // Database Service (Amazon RDS) Data Service to run SQL statements on the cluster.
    new CfnOutput(this, 'SecretArn', {value: secret.secretArn})
    new CfnOutput(this, 'ClusterArn', {value: cluster.clusterArn})
    new CfnOutput(this, 'DbName', {value: dbname})
  }
}

const stackName = 'doc-example-aurora-app'

const app = new App();

new SetupStack(app, stackName);
