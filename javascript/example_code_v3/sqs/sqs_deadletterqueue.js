/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sqs-examples-dead-letter-queues.html

Purpose:
sqs_deadletterqueue.js demonstrates how to enable the dead-letter functionality of an Amazon SQS queue.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_NAME (in command line below)
- DelaySeconds (in code): in seconds
- MessageRetentionPeriod (in code): in seconds

Running the code:
node sqs_deadletterqueue.js REGION
*/
// snippet-start:[sqs.JavaScript.deadLetter.setQueueAttributes]
async function run() {
    try {
        const {SQS, SetQueueAttributesCommand} = require("@aws-sdk/client-sqs");
        const region = process.argv[2];
        const sqs = new SQS(region);
        var params = {
            Attributes: {
                "RedrivePolicy": "{\"deadLetterTargetArn\":\"DEAD_LETTER_QUEUE_ARN\",\"maxReceiveCount\":\"10\"}",
            },
            QueueUrl: "SOURCE_QUEUE_URL"
        };
        const data = await sqs.send(new SetQueueAttributesCommand(params));
        console.log("Success", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[sqs.JavaScript.deadLetter.setQueueAttributes]
exports.run = run;
