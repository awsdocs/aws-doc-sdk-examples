/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html.

Purpose:
sqs_longpolling_receivemessage.js demonstrates how to retrieve messages from an Amazon SQS queue using long-polling support.

Inputs (replace in code):
- SQS_QUEUE_URL
- MaxNumberOfMessages
- WaitTimeSeconds

Running the code:
node sqs_longpolling_receivemessage.js
 */
// snippet-start:[sqs.JavaScript.longPoll.receiveMessageV3]

// Import required AWS SDK clients and commands for Node.js
import { ReceiveMessageCommand } from  "@aws-sdk/client-sqs";
import { sqsClient } from  "./libs/sqsClient.js";

// Set the parameters
const queueURL = "SQS_QUEUE_URL"; // SQS_QUEUE_URL
const params = {
  AttributeNames: ["SentTimestamp"],
  MaxNumberOfMessages: 1,
  MessageAttributeNames: ["All"],
  QueueUrl: queueURL,
  WaitTimeSeconds: 20,
};

const run = async () => {
  try {
    const data = await sqsClient.send(new ReceiveMessageCommand(params));
    console.log("Success, ", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.receiveMessageV3]
// For unit tests only.
// module.exports ={run, params};
