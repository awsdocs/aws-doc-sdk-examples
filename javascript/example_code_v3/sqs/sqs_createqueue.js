/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sqs-examples-using-queues.html

Purpose:
sqs_createqueue.js demonstrates how to create an Amazon SQS standard queue.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_NAME (in command line below)
- DelaySeconds (in code): in seconds
- MessageRetentionPeriod (in code): in seconds

Running the code:
node sqs_createqueue.js REGION SQS_QUEUE_NAME
 */
// snippet-start:[sqs.JavaScript.queues.createQueue]
async function run() {
  try {
    const {SQS, CreateQueueCommand} = require("@aws-sdk/client-sqs");
    const region = process.argv[2];
    const sqs = new SQS(region);
    const params = {
      QueueName: process.argv[3],
      Attributes: {
        'DelaySeconds': '60',
        'MessageRetentionPeriod': '86400'
      }
    };
    const data = await sqs.send(new CreateQueueCommand(params));
    console.log("Success", data.QueueUrl);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.createQueue]
run.exports = run;
