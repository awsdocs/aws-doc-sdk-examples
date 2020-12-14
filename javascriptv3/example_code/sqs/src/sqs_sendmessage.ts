/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-send-receive-messages.html.

Purpose:
sqs_sendmessage.ts demonstrates how to deliver a message to an Amazon SQS queue.

Inputs (replace in code):
- REGION
- SQS_QUEUE_URL (into command line below; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME')

Running the code:
ts-node sqs_sendmessage.ts
 */
// snippet-start:[sqs.JavaScript.messages.sendMessageV3]
// Import required AWS SDK clients and commands for Node.js
const { SQSClient, SendMessageCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  DelaySeconds: 10,
  MessageAttributes: {
    Title: {
      DataType: "String",
      StringValue: "The Whistler",
    },
    Author: {
      DataType: "String",
      StringValue: "John Grisham",
    },
    WeeksOn: {
      DataType: "Number",
      StringValue: "6",
    },
  },
  MessageBody:
    "Information about current NY Times fiction bestseller for week of 12/11/2016.",
  // MessageDeduplicationId: "TheWhistler",  // Required for FIFO queues
  // MessageGroupId: "Group1",  // Required for FIFO queues
  QueueUrl: "SQS_QUEUE_URL", //SQS_QUEUE_URL; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
};

// Create SQS service object
const sqs = new SQSClient({ region: REGION });

const run = async () => {
  try {
    const data = await sqs.send(new SendMessageCommand(params));
    console.log("Success, message sent. MessageID:", data.MessageId);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.messages.sendMessageV3]

