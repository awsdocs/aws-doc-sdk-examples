/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples.html.

Purpose:
sqsClient.js is a helper function that creates an Amazon Simple Queue Service (Amazon SQS) service client.

Inputs (replace in code):
- REGION

*/
// snippet-start:[sqs.JavaScript.createclientv3]
import  { SQSClient } from "@aws-sdk/client-sqs";
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create SQS service object.
const sqsClient = new SQSClient({ region: REGION });
export  { sqsClient };
// snippet-end:[sqs.JavaScript.createclientv3]

