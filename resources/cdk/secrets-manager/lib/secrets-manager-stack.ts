// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { Secret } from 'aws-cdk-lib/aws-secretsmanager';
import {App, Stack, StackProps} from "aws-cdk-lib";

export class SecretsManagerStack extends Stack {
  constructor(scope: App, id: string, props?: StackProps) {
    super(scope, id, props);

    // Define 7 random secrets
    for (let i = 1; i <= 7; i++) {
      new Secret(this, `Secret${i}`, {
        secretName: `mySecret${i}`,
        generateSecretString: {
          secretStringTemplate: JSON.stringify({ username: `user${i}` }),
          generateStringKey: 'password',
        },
      });
    }
  }
}
