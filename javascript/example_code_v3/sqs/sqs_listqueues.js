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
sqs_listqueues.js demonstrates how to retrieve a list of Amazon SQS queues for an AWS account.

Inputs:
- REGION (in command line below)

Running the code:
node sqs_listqueues.js REGION
*/
// snippet-start:[sqs.JavaScript.queues.listQueues]
async function run() {
    try {
        const {SQS, ListQueuesCommand} = require("@aws-sdk/client-sqs");
        const region = process.argv[2];
        const sqs = new SQS(region);
        const data = await sns.send(new ListQueuesCommand({}));
        console.log("Subscription ARN is " + data.SubscriptionArn);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sqs.JavaScript.queues.listQueues]
exports.run = run;
