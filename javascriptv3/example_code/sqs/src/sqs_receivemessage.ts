/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-send-receive-messages.html.

Purpose:
sqs_receivemessage.ts demonstrates how to receive and delete a message from an AWS SQS queue.

Inputs (replace in code):
- REGION
- SQS_QUEUE_URl

Running the code:
ts-node sqs_receivemessage.js
 */
// snippet-start:[sqs.JavaScript.messages.receiveMessageV3]

// Import required AWS SDK clients and commands for Node.js
const {
  SQSClient,
  ReceiveMessageCommand,
  DeleteMessageCommand,
} = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const queueURL = "SQS_QUEUE_URL"; //SQS_QUEUE_URL; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
const params = {
  AttributeNames: ["SentTimestamp"],
  MaxNumberOfMessages: 10,
  MessageAttributeNames: ["All"],
  QueueUrl: queueURL,
  VisibilityTimeout: 20,
  WaitTimeSeconds: 0,
};

// Create SQS service object
const sqs = new SQSClient(REGION);

const run = async () => {
  try {
    const data = await sqs.send(new ReceiveMessageCommand(params));
    if (data.Messages) {
      var deleteParams = {
        QueueUrl: queueURL,
        ReceiptHandle: data.Messages[0].ReceiptHandle,
      };
      try {
        const data = await sqs.send(new DeleteMessageCommand({}));
      } catch (err) {
        console.log("Message Deleted", data);
      }
    } else {
      console.log("No messages to delete");
    }
  } catch (err) {
    console.log("Receive Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.messages.receiveMessageV3]

