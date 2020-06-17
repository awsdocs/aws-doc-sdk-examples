/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sns-examples-publishing-messages.html

Purpose:
sns_publishtotopic.js demonstrates how to send a message to an Amazon SNS topic.

Inputs:
- REGION (in commmand line input below)
- MESSAGE_TEXT (in code)
- TOPIC_ARN  (in commmand line input below)

Running the code:
node sns_publishtotopic.js REGION TOPIC_ARN
 */
// snippet-start:[sns.JavaScript.topics.publishMessages]
async function run() {
    try {
        const {SNS, PublishCommand} = require("@aws-sdk/client-sns");
        const region = process.argv[2];
        const sns = new SNS(region);
        var params = {
            Message: 'MESSAGE_TEXT', /* required */
            TopicArn: process.argv[3]
        };
        const data = await sns.send(new PublishCommand(params));
        console.log(`Message ${params.Message} send sent to the topic ${params.TopicArn}`);
        console.log("MessageID is " + data.MessageId);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.topics.publishMessages]
exports.run = run;
