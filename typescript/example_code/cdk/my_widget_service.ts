//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-comment:[This should be in the bin/ directory]
//snippet-comment:[and only works with widget_service.ts in the lib/ directory]
//snippet-comment:[and widgets.js in the resources/ directory.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Instantiates the stack in my_widget_service-stack.ts.]
//snippet-keyword:[CDK V0.21.0]
//snippet-keyword:[TypeScript]
//snippet-service:[cdk]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-1-9]
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
//snippet-start:[cdk.typescript.my_widget_service]
import cdk = require('@aws-cdk/cdk');
import { MyWidgetServiceStack } from '../lib/my_widget_service-stack';

const app = new cdk.App();
new MyWidgetServiceStack(app, 'MyWidgetServiceStack');
app.run();
//snippet-end:[cdk.typescript.my_widget_service]