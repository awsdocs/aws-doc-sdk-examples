/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-send-receive-messages.html.

Purpose:
sqs_receivemessage.js demonstrates how to receive and delete a message from an Amazon SQS queue.

Inputs:
- REGION (into command line below)
- SQS_QUEUE_URl (into command line below; e.g., 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME')


Running the code:
node sqs_receivemessage.js REGION SQS_QUEUE_URL
 */
// snippet-start:[sqs.JavaScript.messages.receiveMessageV3]
// Import required AWS SDK clients and commands for Node.js
const {SQS, ReceiveMessageCommand, DeleteMessageCommand} = require("@aws-sdk/client-sqs");
// Set the AWS Region
const region = process.argv[2];
// Create SQS service object
const sqs = new SQS(region);
// Set the parameters
var queueURL = process.argv[3]; //SQS_QUEUE_URL
const params = {
    AttributeNames: [
        "SentTimestamp"
    ],
    MaxNumberOfMessages: 10,
    MessageAttributeNames: [
        "All"
    ],
    QueueUrl: queueURL,
    VisibilityTimeout: 20,
    WaitTimeSeconds: 0
};


async function run() {
    try {
        const data = await sqs.send(new ReceiveMessageCommand(params));
        if (data.Messages) {
            var deleteParams = {
                QueueUrl: queueURL,
                ReceiptHandle: data.Messages[0].ReceiptHandle
            };
            try {
                const data = await sqs.send(new DeleteMessageCommand({}));
            } catch (err) {
                console.log("Message Deleted", data);
            }
        }
        else{
            console.log('No messages to delete');
        }
    }
    catch (err){
            console.log("Receive Error", err);
        }
};
run();
// snippet-end:[sqs.JavaScript.messages.receiveMessageV3]
exports.run = run; //for unit tests only
