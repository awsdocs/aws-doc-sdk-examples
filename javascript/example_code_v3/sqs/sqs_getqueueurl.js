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
sqs_getqueueurl.js demonstrates how to retrieve the URL of an Amazon SQS queue.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_NAME (in command line below)

Running the code:
node ssqs_getqueueurl.js REGION SQS_QUEUE_NAME
 */

// snippet-start:[sqs.JavaScript.queues.getQueueUrl]
async function run() {
  try {
    const {SQS, GetQueueUrlCommand} = require("@aws-sdk/client-sqs");
    const region = process.argv[2];
    const sns = new SQS(region);
    const params = {QueueName : process.argv[3]};
    const data = await sns.send(new GetQueueUrlCommand(params));
    console.log("Success", data.QueueUrl);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.getQueueUrl]
exports.run = run;
