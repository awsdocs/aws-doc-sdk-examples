// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include sns-sqs-example-stack.ts, which goes in the lib dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Instantiates a stack using sns-sqs-example-stack]
// snippet-keyword:[CDK V1.0.0
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
// snippet-start:[cdk.typescript.sns-sqs-example]
import * as cdk from '@aws-cdk/core';
import { SnsSqsExampleStack } from '../lib/sns-sqs-example-stack';

const app = new cdk.App();
new SnsSqsExampleStack(app, 'SnsSqsExampleStack');
// snippet-end:[cdk.typescript.sns-sqs-example]
