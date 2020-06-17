/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sns-examples-managing-topics.html

Purpose:
sns_deletetopic.js demonstrates how to delete an Amazon SNS topic and all its subscriptions.

Inputs:
- REGION (in commmand line input below)
- TOPIC_ARN  (in commmand line input below)

Running the code:
node sns_createtopic.js REGION TOPIC_ARN
 */
// snippet-start:[sns.JavaScript.topics.deleteTopic]
// Load the AWS SDK for Node.js
async function run() {
    try {
        const {SNS, DeleteTopicCommand} = require("@aws-sdk/client-sns");
        const region = process.argv[2];
        const sns = new SNS(region);
        const params = {TopicArn: process.argv[3]};
        const data = await sns.send(new DeleteTopicCommand(params));
        console.log("Topic Deleted");
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.topics.deleteTopic]
exports.run = run;
