/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-managing-visibility-timeout.html.

Purpose:
sqs_changingvisibility.ts demonstrates how to change the visibility timeout of a message in an Amazon SQS queue.

Inputs (replace in code):
- REGION (part of queueURL)
- ACCOUNT_ID (part of queueURL)
- QUEUE_NAME (part of queueURL)

Running the code:
ts-node sqs_changingvisibility.ts
 */
// snippet-start:[sqs.JavaScript.visibility.receiveMessageV3]
// Import required AWS SDK clients and commands for Node.js
const {
  SQSClient,
  ReceiveMessageCommand,
  ChangeMessageVisibilityCommand,
} = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const queueURL = "https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME"; // REGION, ACCOUNT_ID, QUEUE_NAME
const params = {
  AttributeNames: ["SentTimestamp"],
  MaxNumberOfMessages: 1,
  MessageAttributeNames: ["All"],
  QueueUrl: queueURL,
};

// Create SQS service object
const sqs = new SQSClient(REGION);

const run = async () => {
  try {
    const data = await sqs.send(new ReceiveMessageCommand(params));
    if (data.Messages != null) {
      try {
        var visibilityParams = {
          QueueUrl: queueURL,
          ReceiptHandle: data.Messages[0].ReceiptHandle,
          VisibilityTimeout: 20, // 20 second timeout
        };
        const results = await sqs.send(
          new ChangeMessageVisibilityCommand(params)
        );
        console.log("Timeout Changed", results);
      } catch (err) {
        console.log("Delete Error", err);
      }
    } else {
      console.log("No messages to change");
    }
  } catch (err) {
    console.log("Receive Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.visibility.receiveMessageV3]

