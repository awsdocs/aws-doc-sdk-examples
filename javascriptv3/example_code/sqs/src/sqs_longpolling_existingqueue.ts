/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html.

Purpose:
sqs_longpolling_existingqueue.ts demonstrates how to change the amount of time an Amazon SQS queue waits for a message to arrive.

Inputs (replace in code):
- REGION
- SQS_QUEUE_URL
- ReceiveMessageWaitTimeSeconds

Running the code:
ts-node sqs_longpolling_existingqueue.ts
 */

// snippet-start:[sqs.JavaScript.longPoll.setQueueAttributesV3]
// Import required AWS SDK clients and commands for Node.js
const { SQSClient, SetQueueAttributesCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Attributes: {
    ReceiveMessageWaitTimeSeconds: "20",
  },
  QueueUrl: "SQS_QUEUE_URL", //SQS_QUEUE_URL; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
};

// Create SQS service object
const sqs = new SQSClient({ region: REGION });

const run = async () => {
  try {
    const data = await sqs.send(new SetQueueAttributesCommand(params));
    console.log(
      "Success, longpolling enabled on queue. RequestID:",
      data.$metadata.requestId
    );
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.setQueueAttributesV3]

