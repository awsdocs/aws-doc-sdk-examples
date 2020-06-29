/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-enable-long-polling.html.

Purpose:
sqs_longpolling_existingqueue.js demonstrates how to change the amount of time an Amazon SQS queue waits for a message to arrive.

Inputs:
- REGION (into command line below)
- SQS_QUEUE_URL (into command line below)
- ReceiveMessageWaitTimeSeconds (into code)

Running the code:
node sqs_longpolling_existingqueue.js REGION SQS_QUEUE_URL
 */

// snippet-start:[sqs.JavaScript.v3.longPoll.setQueueAttributes]
// Import required AWS SDK clients and commands for Node.js
const {SQS, SetQueueAttributesCommand} = require("@aws-sdk/client-sqs");
// Set the AWS Region
const region = process.argv[2];
// Create SQS service object
const sqs = new SQS(region);
// Set the parameters
const params = {
    Attributes: {
        "ReceiveMessageWaitTimeSeconds": "20",
    },
    QueueUrl: process.argv[3] //SQS_QUEUE_URL
};
async function run() {
    try {
        const data = await sqs.send(new SetQueueAttributesCommand(params));
        console.log("Success, longpolling enabled on queue. RequestID:", data.$metadata.requestId);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sqs.JavaScript.v3.longPoll.setQueueAttributes]
exports.run = run; //for unit tests only
