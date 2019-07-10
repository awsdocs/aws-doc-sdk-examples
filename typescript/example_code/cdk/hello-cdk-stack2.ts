// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include HelloCdk.ts, which goes in the bin dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Creates an S3 bucket with encryption and versioning enabled.]
// snippet-keyword:[CDK V0.24.1]
// snippet-keyword:[s3.Bucket function]
// snippet-keyword:[TypeScript]
// snippet-service:[cdk]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-2-8]
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
// snippet-start:[cdk.typescript.hello-cdk-stack.version2]
import core = require('@aws-cdk/core');
import s3 = require('@aws-cdk/aws-s3');

export class HelloCdkStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    // Don't change the formatting of this section
    // snippet-start:[cdk.typescript.hello-cdk-stack.version2_bucket]
new s3.Bucket(this, 'MyFirstBucket', {
  versioned: true,
  encryption: s3.BucketEncryption.KmsManaged
});
    // snippet-end:[cdk.typescript.hello-cdk-stack.version2_bucket]    
  }
}
// snippet-end:[cdk.typescript.hello-cdk-stack.version2]
