/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sqs-examples-send-receive-messages.html

Purpose:
sqs_sendmessage.js demonstrates how to deliver a message to an Amazon SQS queue.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_URL (in command line below)

Running the code:
node sqs_sendmessage.js REGION SQS_QUEUE_URL
 */
// snippet-start:[sqs.JavaScript.messages.sendMessage]
async function run() {
  try {
    const {SQS, SendMessageCommand} = require("@aws-sdk/client-sqs");
    const region = process.argv[2];
    const sqs = new SQS(region);
    const params = {
      DelaySeconds: 10,
      MessageAttributes: {
        "Title": {
          DataType: "String",
          StringValue: "The Whistler"
        },
        "Author": {
          DataType: "String",
          StringValue: "John Grisham"
        },
        "WeeksOn": {
          DataType: "Number",
          StringValue: "6"
        }
      },
      MessageBody: "Information about current NY Times fiction bestseller for week of 12/11/2016.",
      // MessageDeduplicationId: "TheWhistler",  // Required for FIFO queues
      // MessageGroupId: "Group1",  // Required for FIFO queues
      QueueUrl: process.argv[3]
    };
    const data = await sqs.send(new SendMessageCommand(params));
    console.log("Success", data.MessageId);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.messages.sendMessage]
exports.run = run;
