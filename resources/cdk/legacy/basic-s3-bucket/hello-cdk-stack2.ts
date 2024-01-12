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
// snippet-start:[cdk.typescript.hello-cdk-stack.version2]
import * as core from "@aws-cdk/core";
import * as s3 from "@aws-cdk/aws-s3";

export class HelloCdkStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    // Don't change the formatting of this section
    // snippet-start:[cdk.typescript.hello-cdk-stack.version2_bucket]
    new s3.Bucket(this, "MyFirstBucket", {
      versioned: true,
      encryption: s3.BucketEncryption.KMS_MANAGED,
    });
    // snippet-end:[cdk.typescript.hello-cdk-stack.version2_bucket]
  }
}
// snippet-end:[cdk.typescript.hello-cdk-stack.version2]
