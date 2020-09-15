/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html.

Purpose:
sqs_longpolling_createqueue.ts demonstrates how to create an Amazon SQS queue that waits for a message to arrive.

Inputs (replace in code):
- REGION
- SQS_QUEUE_NAME

Running the code:
ts-node sqs_longpolling_createqueue.ts
*/
// snippet-start:[sqs.JavaScript.longPoll.createQueueV3]

// Import required AWS SDK clients and commands for Node.js
const { SQS, CreateQueueCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  QueueName: "SQS_QUEUE_NAME", //SQS_QUEUE_URL; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
  Attributes: {
    ReceiveMessageWaitTimeSeconds: "20",
  },
};

// Create SQS service object
const sqs = new SQS(REGION);

const run = async () => {
  try {
    const data = await sqs.send(new CreateQueueCommand(params));
    console.log(
      "Success, new SQS queue created. SQS queue URL:",
      data.QueueUrl
    );
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.createQueueV3]
export = {run}; //for unit tests only
