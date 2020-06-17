/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sqs-examples-managing-visibility-timeout.html

Purpose:
sqs_changingvisibility.js demonstrates how to change the visibility timeout of a message in an Amazon SQS queue.

Inputs:
- REGION (in code)
- ACCOUNT-ID (in code)
- QUEUE-NAME (in code)

Running the code:
node sqs_changingvisibility.js  REGION
 */
// snippet-start:[sqs.JavaScript.visibility.receiveMessage]
async function run() {
  try {
    const {SQS, ReceiveMessageCommand, ChangeMessageVisibilityCommand} = require("@aws-sdk/client-sqs");
    const region = process.argv[2];
    const sqs = new SQS(region);
    const queueURL = 'https://sqs.REGION.amazonaws.com/ACCOUNT-ID/QUEUE-NAME'
    const params = {
      AttributeNames: ['SentTimestamp'],
      MaxNumberOfMessages: 1,
      MessageAttributeNames: ['All'],
      QueueUrl: queueURL
    };
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
// snippet-end:[sqs.JavaScript.visibility.receiveMessage]
exports.run = run;
