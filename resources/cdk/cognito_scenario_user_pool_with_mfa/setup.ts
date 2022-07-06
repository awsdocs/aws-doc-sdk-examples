// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Defines an AWS CloudFormation stack that creates AWS resources for Amazon Cognito
 * scenarios. The scripts in this example create the following resources:
 *
 * * An Amazon Cognito user pool that is configured to do the following:
 *   * Allow self sign-up
 *   * Verify by email
 *   * Require multi-factor authentication (MFA)
 *   * Allow device tracking
 * * An associated client application that can authenticate with the user pool
 */

import 'source-map-support/register';
import {Construct} from "constructs";
import {App, CfnOutput, RemovalPolicy, Stack, StackProps} from 'aws-cdk-lib';
import {Mfa, UserPool} from 'aws-cdk-lib/aws-cognito'

export class SetupStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const exampleUserPoolName = `doc-example-scenario-user-pool`;
    const exampleUserPool = new UserPool(this, 'doc-example-scenario-user-pool-id', {
      userPoolName: exampleUserPoolName,
      selfSignUpEnabled: true,
      autoVerify: {email: true},
      standardAttributes: {
        email: {
          required: true,
          mutable: false,
        }
      },
      signInCaseSensitive: false,
      mfa: Mfa.REQUIRED,
      mfaSecondFactor: {otp: true, sms: false},
      deviceTracking: {
        challengeRequiredOnNewDevice: true,
        deviceOnlyRememberedOnUserPrompt: false
      },
      removalPolicy: RemovalPolicy.DESTROY
    });

    const exampleUserPoolClientName = `doc-example-scenario-client`;
    const exampleUserPoolClient = exampleUserPool.addClient('doc-example-scenario-client-id', {
      userPoolClientName: exampleUserPoolClientName,
      authFlows: {adminUserPassword: true, userPassword: true}
    });

    new CfnOutput(this, 'UserPoolId', {value: exampleUserPool.userPoolId})
    new CfnOutput(this, 'ClientId', {value: exampleUserPoolClient.userPoolClientId})
  }
}

const stackName = 'doc-example-cognito-scenario-signup-user-with-mfa'

const app = new App();

new SetupStack(app, stackName);
