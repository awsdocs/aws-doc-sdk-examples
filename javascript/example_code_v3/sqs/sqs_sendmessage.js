/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-send-receive-messages.html.

Purpose:
sqs_sendmessage.js demonstrates how to deliver a message to an Amazon SQS queue.

Inputs (replace in code):
- REGION
- SQS_QUEUE_URL (into command line below; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME')

Running the code:
node sqs_sendmessage.js
 */
// snippet-start:[sqs.JavaScript.messages.sendMessageV3]

// Import required AWS SDK clients and commands for Node.js
const {SQS, SendMessageCommand} = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  DelaySeconds: 10,
  MessageAttributes: {
    "Title": {
      DataType: "String",
      StringValue: "The Whistler"
    },
    "Author": {
      DataType: "String",
      StringValue: "John Grisham"
    },
    "WeeksOn": {
      DataType: "Number",
      StringValue: "6"
    }
  },
  MessageBody: "Information about current NY Times fiction bestseller for week of 12/11/2016.",
  // MessageDeduplicationId: "TheWhistler",  // Required for FIFO queues
  // MessageGroupId: "Group1",  // Required for FIFO queues
  QueueUrl: "SQS_QUEUE_URL" //SQS_QUEUE_URL; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
};

// Create SQS service object
const sqs = new SQS(REGION);

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
exports.run = run; //for unit tests only
