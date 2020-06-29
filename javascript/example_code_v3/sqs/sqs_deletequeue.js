/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-using-queues.html.

Purpose:
sqs_deletequeue.js demonstrates how to delete an Amazon SQS queue.

Inputs:
- REGION (into command line below)
- SQS_QUEUE_URl (into command line below; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME')
- DelaySeconds (into code; enter in seconds)
- MessageRetentionPeriod (into code; enter in seconds)

Running the code:
node sqs_deletequeue.js REGION SQS_QUEUE_URL
*/
// snippet-start:[sqs.JavaScript.v3.queues.deleteQueue]
// Import required AWS SDK clients and commands for Node.js
const {SQS, DeleteQueueCommand} = require("@aws-sdk/client-sqs");
// Set the AWS Region
const region = process.argv[2];
// Create SQS service object
const sns = new SQS(region);
// Set the parameters
const params = {QueueUrl : process.argv[3]}; //SQS_QUEUE_URL

async function run() {
  try {
    const data = await sns.send(new DeleteQueueCommand(params));
    console.log("Success, queue deleted. RequestID:", data.$metadata.requestId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.v3.queues.deleteQueue]
exports.run = run; //for unit tests only
