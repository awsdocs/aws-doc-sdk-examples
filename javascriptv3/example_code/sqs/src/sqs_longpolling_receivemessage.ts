/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html.

Purpose:
sqs_longpolling_receivemessage.ts demonstrates how to retrieve messages from an AWS SQS queue using long-polling support.

Inputs (replace in code):
- REGION
- SQS_QUEUE_URL
- MaxNumberOfMessages
- WaitTimeSeconds

Running the code:
ts-node sqs_longpolling_receivemessage.ts
 */
// snippet-start:[sqs.JavaScript.longPoll.receiveMessageV3]

// Import required AWS SDK clients and commands for Node.js
const { SQSClient, ReceiveMessageCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const queueURL = "SQS_QUEUE_URL"; // SQS_QUEUE_URL
const params = {
  AttributeNames: ["SentTimestamp"],
  MaxNumberOfMessages: 1,
  MessageAttributeNames: ["All"],
  QueueUrl: queueURL,
  WaitTimeSeconds: 20,
};

// Create SQS service object
const sqs = new SQSClient(REGION);

const run = async () => {
  try {
    const data = await sqs.send(new ReceiveMessageCommand(params));
    console.log("Success, ", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.receiveMessageV3]

