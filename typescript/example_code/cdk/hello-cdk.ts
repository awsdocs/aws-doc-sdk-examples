//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-comment:[This is a full sample when you include hello-cdk-stack.ts, which goes in the lib dir.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Instantiates the stack in hello-cdk-stack.ts.]
//snippet-keyword:[CDK V0.21.0]
//snippet-keyword:[TypeScript]
//snippet-service:[cdk]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-1-8]
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
import cdk = require('@aws-cdk/cdk');

import { HelloCdkStack } from '../lib/hello-cdk-stack';

const app = new cdk.App();
new HelloCdkStack(app, 'HelloCdkStack');

app.run();
