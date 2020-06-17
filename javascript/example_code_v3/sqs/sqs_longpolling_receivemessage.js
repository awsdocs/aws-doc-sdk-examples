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
sqs_longpolling_receivemessage.js demonstrates how to retrieve messages from an Amazon SQS queue using long-poll support.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_URL (in command line below)
- MaxNumberOfMessages (in code)
- WaitTimeSeconds (in code)

Running the code:
node sqs_longpolling_createqueue.js REGION SQS_QUEUE_URL
 */
// snippet-start:[sqs.JavaScript.longPoll.receiveMessage]
async function run() {
    try {
        const {SQS, ReceiveMessageCommand} = require("@aws-sdk/client-sqs");
        const region = process.argv[2];
        const sqs = new SQS(region);
        var queueURL = process.argv[3];
        var params = {
            AttributeNames: [
                "SentTimestamp"
            ],
            MaxNumberOfMessages: 1,
            MessageAttributeNames: [
                "All"
            ],
            QueueUrl: queueURL,
            WaitTimeSeconds: 20
        };
        const data = await sqs.send(new ReceiveMessageCommand({}));
        console.log("Success", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[sqs.JavaScript.longPoll.receiveMessage]
exports.run = run;
