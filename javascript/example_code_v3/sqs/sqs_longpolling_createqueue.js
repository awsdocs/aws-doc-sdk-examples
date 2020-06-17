/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sqs-examples-enable-long-polling.html

Purpose:
sqs_longpolling_createqueue.test.js demonstrates how to create an Amazon SQS queue that waits for a message to arrive.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_NAME (in command line below)

Running the code:
node sqs_longpolling_createqueue.js REGION SQS_QUEUE_NAME
*/
// snippet-start:[sqs.JavaScript.longPoll.createQueue]
async function run() {
  try {
    const {SQS, CreateQueueCommand} = require("@aws-sdk/client-sqs");
    const region = process.argv[2];
    const sqs = new SQS(region);
    const params =  {
      QueueName: process.argv[3],
      Attributes: {
        'ReceiveMessageWaitTimeSeconds': '20',
      }
    };
    const data = await sqs.send(new CreateQueueCommand({}));
    console.log("Success", data.QueueUrl);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.createQueue]
exports.run = run;
