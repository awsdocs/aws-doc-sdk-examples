// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/* eslint-disable -- This file existed pre-eslint configuration. Fix the next time the file is touched. */

/*
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/api-gateway-invoking-lambda-example.html.
Purpose:
snsClient.js is a helper function that creates an Amazon Simple Notification Service (Amazon SNS) service client.

Inputs (replace in code):
-REGION

*/
// snippet-start:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.sns]

const { SNSClient } = require("@aws-sdk/client-sns");
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create an Amazon SNS service client object.
const snsClient = new SNSClient({ region: REGION });
module.exports = { snsClient };
// snippet-end:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.sns]
