/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/serverless-step-functions-example.html.

Purpose:
sesClient.js is a helper function that creates an Amazon Simple Email Service (Amazon SES) service client.

Inputs (replace in code):
-REGION

*/
// snippet-start:[ses.JavaScript.step-functions.createclientv3]

const { SESClient } = require ( "@aws-sdk/client-ses" );
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create an Amazon SNS service client object.
const sesClient = new SESClient({ region: REGION });
module.exports = { sesClient };
// snippet-end:[ses.JavaScript.step-functions.createclientv3]
