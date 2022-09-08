/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-managing-visibility-timeout.html.

Purpose:
sqs_changingvisibility.js demonstrates how to change the visibility timeout of a message in an Amazon SQS queue.

Inputs (replace in code): (part of queueURL)
- ACCOUNT_ID (part of queueURL)
- QUEUE_NAME (part of queueURL)

Running the code:
node sqs_changingvisibility.js
 */
// snippet-start:[sqs.JavaScript.visibility.receiveMessageV3]
// Import required AWS SDK clients and commands for Node.js
import {
  ReceiveMessageCommand,
  ChangeMessageVisibilityCommand,
} from  "@aws-sdk/client-sqs";
import { sqsClient } from  "./libs/sqsClient.js";

// Set the parameters
const queueURL = "https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME"; // REGION, ACCOUNT_ID, QUEUE_NAME
const params = {
  AttributeNames: ["SentTimestamp"],
  MaxNumberOfMessages: 1,
  MessageAttributeNames: ["All"],
  QueueUrl: queueURL,
};


const run = async () => {
  try {
    const data = await sqsClient.send(new ReceiveMessageCommand(params));
    if (data.Messages != null) {
      try {
        const visibilityParams = {
          QueueUrl: queueURL,
          ReceiptHandle: data.Messages[0].ReceiptHandle,
          VisibilityTimeout: 20, // 20 second timeout
        };
        const results = await sqsClient.send(
            new ChangeMessageVisibilityCommand(visibilityParams)
        );
        console.log("Timeout Changed", results);
      } catch (err) {
        console.log("Delete Error", err);
      }
    } else {
      console.log("No messages to change");
    }
    return data; // For unit tests.
  } catch (err) {
    console.log("Receive Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.visibility.receiveMessageV3]
// For unit tests only.
// module.exports ={run, params};
