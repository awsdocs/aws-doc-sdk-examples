/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-using-queues.html.

Purpose:
sqs_getqueueurl.js demonstrates how to retrieve the URL of an Amazon SQS queue.

Inputs:
- REGION (into command line below)
- SQS_QUEUE_NAME (into command line below)

Running the code:
node ssqs_getqueueurl.js REGION SQS_QUEUE_NAME
 */

// snippet-start:[sqs.JavaScript.v3.queues.getQueueUrl]
// Import required AWS SDK clients and commands for Node.js
const {SQS, GetQueueUrlCommand} = require("@aws-sdk/client-sqs");
// Set the AWS Region
const region = process.argv[2];
// Create SQS service object
const sns = new SQS(region);
// Set the parameters
const params = {QueueName : process.argv[3]}; //SQS_QUEUE_NAME

async function run() {
  try {
    const data = await sns.send(new GetQueueUrlCommand(params));
    console.log("Success, SQS queue URL:", data.QueueUrl);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.v3.queues.getQueueUrl]
exports.run = run; //for unit tests only
