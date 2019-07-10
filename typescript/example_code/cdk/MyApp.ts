// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include MyApp-stack.ts, which goes in the lib dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[MyApp.ts instantiates two stacks using MyApp-stack]
// snippet-keyword:[CDK V0.27.0]
// snippet-keyword:[TypeScript]
// snippet-service:[cdk]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-4-2]
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
// snippet-start:[cdk.typescript.MyApp]
import core = require("@aws-cdk/core");
import { MyStack } from "../lib/MyApp-stack";

const app = new core.App();

new MyStack(app, "MyWestCdkStack", {
  env: {
    region: "us-west-2"
  },
  enc: false
});

new MyStack(app, "MyEastCdkStack", {
  env: {
    region: "us-east-1"
  },
  enc: true
});
// snippet-end:[cdk.typescript.MyApp]
