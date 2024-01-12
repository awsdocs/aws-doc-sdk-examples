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
// snippet-start:[cdk.typescript.HelloCdk]
import * as cdk from "@aws-cdk/core";
import { HelloCdkStack } from "../lib/HelloCdk-stack";

const app = new cdk.App();
new HelloCdkStack(app, "HelloCdkStack");
// snippet-end:[cdk.typescript.HelloCdk]
