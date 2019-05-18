// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include SecretsManager.ts, which goes in the bin dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[SecretsManager-stack.ts ???]
// snippet-keyword:[CDK V0.28.0]
// snippet-keyword:[TypeScript]
// snippet-service:[cdk]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-4-5]
// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import cdk = require("@aws-cdk/cdk");
import s3 = require("@aws-cdk/aws-s3");
// snippet-start:[cdk.typescript.secrets_manager_stack_code]
import sm = require("@aws-cdk/aws-secretsmanager");

export class SecretsManagerStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const secret = sm.Secret.fromSecretAttributes(this, "ImportedSecret", {
      secretArn:
        "arn:aws:secretsmanager:<region>:<account-id-number>:secret:<secret-name>-<random-6-characters>"
      // If the secret is encrypted using a KMS-hosted CMK, either import or reference that key:
      // encryptionKey,
    });
// snippet-end:[cdk.typescript.secrets_manager_stack_code]

    new s3.Bucket(this, "MySecretBucket", {
      bucketName: secret.secretValue.toString()
    });
  }
}
// snippet-end:[cdk.typescript.secrets_manager_stack]
