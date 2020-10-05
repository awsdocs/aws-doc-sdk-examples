/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-using-queues.html.

Purpose:
sqs_getqueueurl.ts demonstrates how to retrieve the URL of an AWS SQS queue.

Inputs (replace in code):
- REGION
- SQS_QUEUE_NAME

Running the code:
ts-node ssqs_getqueueurl.ts
 */

// snippet-start:[sqs.JavaScript.queues.getQueueUrlV3]
// Import required AWS SDK clients and commands for Node.js
const { SQSClient, GetQueueUrlCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { QueueName: "SQS_QUEUE_NAME" }; //SQS_QUEUE_NAME

// Create SQS service object
const sns = new SQSClient(REGION);

const run = async () => {
  try {
    const data = await sns.send(new GetQueueUrlCommand(params));
    console.log("Success, SQS queue URL:", data.QueueUrl);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.getQueueUrlV3]

