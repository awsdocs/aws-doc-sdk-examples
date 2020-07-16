/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-using-queues.html.

Purpose:
sqs_getqueueurl.js demonstrates how to retrieve the URL of an Amazon SQS queue.

Inputs (replace in code):
- REGION
- SQS_QUEUE_NAME

Running the code:
node ssqs_getqueueurl.js
 */

// snippet-start:[sqs.JavaScript.queues.getQueueUrlV3]

// Import required AWS SDK clients and commands for Node.js
const { SQS, GetQueueUrlCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = { QueueName: "SQS_QUEUE_NAME" }; //SQS_QUEUE_NAME

// Create SQS service object
const sns = new SQS(REGION);

const run = async () => {
  try {
    const data = await sns.send(new GetQueueUrlCommand(params));
    console.log("Success, SQS queue URL:", data.QueueUrl);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.getQueueUrlV3]
exports.run = run; //for unit tests only
