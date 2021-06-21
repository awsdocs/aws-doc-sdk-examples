/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Purpose:
snsClient.js is a helper function that creates an Amazon Simple Notification Service (Amazon SNS) service client.

Inputs (replace in code):
-REGION

*/
// snippet-start:[sns.JavaScript.scheduledevents.createclientv3]

import { SNSClient } from "@aws-sdk/client-sns";
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create an Amazon SNS service client object.
const snsClient = new SNSClient({ region: REGION });
export { snsClient };
// snippet-end:[sns.JavaScript.scheduledevents.createclientv3]


