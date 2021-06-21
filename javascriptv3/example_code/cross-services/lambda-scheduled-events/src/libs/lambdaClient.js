/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Purpose:
lambdaClient.js is a helper function that creates an Amazon Lambda service client.

Inputs (replace in code):
-REGION

*/
// snippet-start:[lambda.JavaScript.scheduledevents.createclientv3]

import { LambdaClient } from "@aws-sdk/client-lambda";
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create an Amazon Lambda service client object.
const lambdaClient = new LambdaClient({ region: REGION });
export { lambdaClient };
// snippet-end:[lambda.JavaScript.scheduledevents.createclientv3]
