/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sqs-examples-send-receive-messages.html

Purpose:
sqs_receivemessage.js demonstrates how to receive and delete a message from an Amazon SQS queue.

Inputs:
- REGION (in command line below)
- SQS_QUEUE_URL (in command line below)
- MaxNumberOfMessages (in code)
- WaitTimeSeconds (in code)

Running the code:
node sqs_receivemessage.js REGION SQS_QUEUE_URL
 */
// snippet-start:[sqs.JavaScript.messages.receiveMessage]
async function run() {
    try {
        const {SQS, ReceiveMessageCommand, DeleteMessageCommand} = require("@aws-sdk/client-sqs");
        const region = process.argv[2];
        const sqs = new SQS(region);
        var queueURL = process.argv[3];
        const params = {
            AttributeNames: [
                "SentTimestamp"
            ],
            MaxNumberOfMessages: 10,
            MessageAttributeNames: [
                "All"
            ],
            QueueUrl: queueURL,
            VisibilityTimeout: 20,
            WaitTimeSeconds: 0
        };
        const data = await sqs.send(new ReceiveMessageCommand(params));
        if (data.Messages) {
            var deleteParams = {
                QueueUrl: queueURL,
                ReceiptHandle: data.Messages[0].ReceiptHandle
            };
            try {
                const data = await sqs.send(new DeleteMessageCommand({}));
            } catch (err) {
                console.log("Message Deleted", data);
            }
        }
        else{
            console.log('No messages to delete');
        }
    }
    catch (err){
            console.log("Receive Error", err);
        }
};
run();
// snippet-end:[sqs.JavaScript.messages.receiveMessage]
exports.run = run;
