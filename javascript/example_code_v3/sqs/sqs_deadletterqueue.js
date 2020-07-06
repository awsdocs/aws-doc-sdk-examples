/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-dead-letter-queues.html.

Purpose:
sqs_deadletterqueue.js demonstrates how to enable the dead-letter functionality of an Amazon SQS queue.

Inputs:
- REGION (into command line below)
- SQS_QUEUE_URL (into command line below)
- DEAD_LETTER_QUEUE_ARN (into code)

Running the code:
node sqs_deadletterqueue.js REGION SQS_QUEUE_URL
*/
// snippet-start:[sqs.JavaScript.v3.deadLetter.setQueueAttributes]
// Import required AWS SDK clients and commands for Node.js
const {SQS, SetQueueAttributesCommand} = require("@aws-sdk/client-sqs");
// Set the AWS Region
const region = process.argv[2];
// Create SQS service object
const sqs = new SQS(region);
// Set the parameters
var params = {
    Attributes: {
        "RedrivePolicy": "{\"deadLetterTargetArn\":\"DEAD_LETTER_QUEUE_ARN\"," +
            "\"maxReceiveCount\":\"10\"}",
    },
    QueueUrl: process.argv[3] //SQS_QUEUE_URL
};

async function run() {
    try {
        const data = await sqs.send(new SetQueueAttributesCommand(params));
        console.log("Success, source queues configured. RequestID:", data.$metadata.requestId);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[sqs.JavaScript.v3.deadLetter.setQueueAttributes]
exports.run = run; //for unit tests only
