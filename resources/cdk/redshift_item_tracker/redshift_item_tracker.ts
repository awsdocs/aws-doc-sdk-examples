// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Defines an AWS CloudFormation stack that creates AWS resources for Amazon Redshift
 * sample applications.
 *
 * * An AWS Secrets Manager secret that contains administrator credentials in a format
 *   that can be used by a Redshift database.
 * * A Redshift cluster and database configured to use the credentials from the secret.
  */

import 'source-map-support/register';
import {App, Stack, StackProps, aws_redshift, CfnOutput} from 'aws-cdk-lib';
import { Construct } from 'constructs';
import {Secret} from "aws-cdk-lib/aws-secretsmanager";

export class RedshiftItemTrackerStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const username = 'workitemsadmin'
    const secret: Secret = new Secret(this, 'doc-example-redshift-work-items-secret', {
        generateSecretString: {
            secretStringTemplate: JSON.stringify({
              username: username
            }),
            generateStringKey: 'password',
            excludePunctuation: true,
            includeSpace: false,
        }
    });

    const clusterId = 'doc-example-work-items-cluster'
    const dbName = 'workitemtracker'
    new aws_redshift.CfnCluster(this, 'doc-example-work-items-cluster', {
      clusterIdentifier: clusterId,
      clusterType: 'multi-node',
      dbName: dbName,
      masterUsername: username,
      masterUserPassword: secret.secretValueFromJson('password').unsafeUnwrap(),
      nodeType: 'dc2.large',
      numberOfNodes: 2
    })

    new CfnOutput(this, 'ClusterId', {value: clusterId})
    new CfnOutput(this, 'SecretArn', {value: secret.secretArn})
    new CfnOutput(this, 'Database', {value: dbName})
  }
}

const app = new App();
new RedshiftItemTrackerStack(app, 'RedshiftItemTrackerStack');
