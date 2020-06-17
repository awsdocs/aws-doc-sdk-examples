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
sqs_longpolling_existingqueue.js demonstrates how to change the amount of time an Amazon SQS queue waits for a message to arrive.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_URL (in command line below)
- ReceiveMessageWaitTimeSeconds (in code)

Running the code:
node sqs_longpolling_createqueue.js REGION SQS_QUEUE_URL
 */

// snippet-start:[sqs.JavaScript.longPoll.setQueueAttributes]
async function run() {
    try {
        const {SQS, SetQueueAttributesCommand} = require("@aws-sdk/client-sqs");
        const region = process.argv[2];
        const sqs = new SQS(region);
        const params = {
            Attributes: {
                "ReceiveMessageWaitTimeSeconds": "20",
            },
            QueueUrl: process.argv[3]
        };
        const data = await sqs.send(new SetQueueAttributesCommand({}));
        console.log("Success", data);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.setQueueAttributes]
exports.run = run;
