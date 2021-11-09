// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include s3-example.ts, which goes in the bin dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Creates an S3 bucket with encryption and versioning enabled.]
// snippet-keyword:[CDK V1.0.0]
// snippet-keyword:[s3.Bucket function]
// snippet-keyword:[TypeScript]
// snippet-sourcesyntax:[javascript]
// snippet-service:[cdk]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-7-11]
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
// snippet-start:[cdk.typescript.s3-example-stack]
import * as core from '@aws-cdk/core';
import * as s3 from '@aws-cdk/aws-s3';

export class S3ExampleStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    // Don't change the formatting of this section
    // snippet-start:[cdk.typescript.s3-example-stack_bucket]
new s3.Bucket(this, 'MyFirstBucket', {
  versioned: true,
  encryption: s3.BucketEncryption.KMS_MANAGED
});
    // snippet-end:[cdk.typescript.s3-example-stack_bucket]
  }
}
// snippet-end:[cdk.typescript.s3-example-stack]
