// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of the
// License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
// snippet-start:[cdk.typescript.secrets_manager_stack]
import * as core from "@aws-cdk/core";
import * as s3 from "@aws-cdk/aws-s3";
// snippet-start:[cdk.typescript.secrets_manager_stack_code]
import * as sm from "@aws-cdk/aws-secretsmanager";

export class SecretsManagerStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    const secret = sm.Secret.fromSecretAttributes(this, "ImportedSecret", {
      secretArn:
        "arn:aws:secretsmanager:<region>:<account-id-number>:secret:<secret-name>-<random-6-characters>",
      // If the secret is encrypted using a KMS-hosted CMK, either import or reference that key:
      // encryptionKey: ...
    });
    // snippet-end:[cdk.typescript.secrets_manager_stack_code]

    s3.Bucket.fromBucketName(
      this,
      "MySecretBucket",
      secret.secretValue.toString()
    );
  }
}
// snippet-end:[cdk.typescript.secrets_manager_stack]
