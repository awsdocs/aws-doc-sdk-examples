/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-using-queues.html.

Purpose:
sqs_listqueues.ts demonstrates how to retrieve a list of Amazon SQS queues for an AWS account.

Inputs (replace in code):
- REGION

Running the code:
ts-node sqs_listqueues.ts
*/
// snippet-start:[sqs.JavaScript.queues.listQueuesV3]

// Import required AWS SDK clients and commands for Node.js
const { SQS, ListQueuesCommand } = require("@aws-sdk/client-sqs");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Create SQS service object
const sqs = new SQS(REGION);

const run = async () => {
  try {
    const data = await sqs.send(new ListQueuesCommand({}));
    console.log("Subscription ARN is " + data.SubscriptionArn);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.listQueuesV3]
export = {run}; //for unit tests only
