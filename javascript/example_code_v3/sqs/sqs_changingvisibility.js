/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-managing-visibility-timeout.html.

Purpose:
sqs_changingvisibility.js demonstrates how to change the visibility timeout of a message in an Amazon SQS queue.

Inputs:
- REGION (into code; part of queueURL)
- ACCOUNT_ID (into code; part of queueURL)
- QUEUE_NAME (into code; part of queueURL)

Running the code:
node sqs_changingvisibility.js
 */
// snippet-start:[sqs.JavaScript.v3.visibility.receiveMessage]
// Import required AWS SDK clients and commands for Node.js
const {SQS, ReceiveMessageCommand, ChangeMessageVisibilityCommand} = require("@aws-sdk/client-sqs");
// Set the AWS Region
const region = process.argv[2];
// Create SQS service object
const sqs = new SQS(region);
// Set the parameters
const queueURL = 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
const params = {
  AttributeNames: ['SentTimestamp'],
  MaxNumberOfMessages: 1,
  MessageAttributeNames: ['All'],
  QueueUrl: queueURL
};

async function run() {
  try {
    const data = await sqs.send(new ReceiveMessageCommand(params));
    if (data.Messages != null) {
      try {
        var visibilityParams = {
          QueueUrl: queueURL,
          ReceiptHandle: data.Messages[0].ReceiptHandle,
          VisibilityTimeout: 20 // 20 second timeout
        }
        const results = await sqs.send(new ChangeMessageVisibilityCommand(params));
        console.log('Timeout Changed', results)
      } catch (err) {
        console.log('Delete Error', err);
      }
    }
    else{
      console.log('No messages to change');
    }
  }
    catch(err){
        console.log('Receive Error', err);
      }
};
run();
// snippet-end:[sqs.JavaScript.v3.visibility.receiveMessage]
exports.run = run; //for unit tests only
