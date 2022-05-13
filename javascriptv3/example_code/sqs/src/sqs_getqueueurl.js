/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-using-queues.html.

Purpose:
sqs_getqueueurl.js demonstrates how to retrieve the URL of an Amazon Simple Queue Service (Amazon SQS) queue.

Inputs (replace in code):
- SQS_QUEUE_NAME

Running the code:
node ssqs_getqueueurl.js
 */

// snippet-start:[sqs.JavaScript.queues.getQueueUrlV3]
// Import required AWS SDK clients and commands for Node.js
import { GetQueueUrlCommand } from  "@aws-sdk/client-sqs";
import { sqsClient } from  "./libs/sqsClient.js";

// Set the parameters
const params = { QueueName: "SQS_QUEUE_NAME" };

const run = async () => {
  try {
    const data = await sqsClient.send(new GetQueueUrlCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.getQueueUrlV3]
// For unit tests only.
// module.exports ={run, params};
