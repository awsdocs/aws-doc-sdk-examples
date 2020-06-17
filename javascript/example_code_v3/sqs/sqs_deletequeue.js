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
sqs_deletequeue.js demonstrates how to delete an Amazon SQS queue.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_NAME (in command line below)
- DelaySeconds (in code): in seconds
- MessageRetentionPeriod (in code): in seconds

Running the code:
node sqs_deletequeue.js REGION SQS_QUEUE_URL
*/
// snippet-start:[sqs.JavaScript.queues.deleteQueue]
// Load the AWS SDK for Node.js
async function run() {
  try {
    const {SQS, DeleteQueueCommand} = require("@aws-sdk/client-sqs");
    const region = process.argv[2];
    const sns = new SQS(region);
    const params = {QueueUrl : process.argv[3]};
    const data = await sns.send(new DeleteQueueCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sqs.JavaScript.queues.deleteQueue]
exports.run = run;
