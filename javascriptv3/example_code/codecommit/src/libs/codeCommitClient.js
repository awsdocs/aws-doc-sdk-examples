/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
codeBuildClient.js is a helper function that creates the Amazon CodeBuild service clients.

Inputs (replace in code):
- REGION

*/
// snippet-start:[code-commit.JavaScript.codeCommitClient]
import { CodeCommitClient } from "@aws-sdk/client-codecommit";

const REGION = "eu-west-1";

// Create an AWS CodeBuild service client object.
const codeCommitClient = new CodeCommitClient({region: REGION});

export { codeCommitClient };
// snippet-end:[code-commit.JavaScript.codeCommitClient]
