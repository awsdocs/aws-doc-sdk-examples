/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html.

Purpose:
sqs_longpolling_createqueue.js demonstrates how to create an Amazon SQS queue that waits for a message to arrive.

Inputs (replace in code):
- SQS_QUEUE_NAME

Running the code:
node sqs_longpolling_createqueue.js
*/
// snippet-start:[sqs.JavaScript.longPoll.createQueueV3]
// Import required AWS SDK clients and commands for Node.js
import { CreateQueueCommand } from  "@aws-sdk/client-sqs";
import { sqsClient } from  "./libs/sqsClient.js";

// Set the parameters
const params = {
  QueueName: "SQS_QUEUE_NAME", //SQS_QUEUE_URL; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
  Attributes: {
    ReceiveMessageWaitTimeSeconds: "20",
  },
};

const run = async () => {
  try {
    const data = await sqsClient.send(new CreateQueueCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.createQueueV3]
// For unit tests only.
// module.exports ={run, params};
