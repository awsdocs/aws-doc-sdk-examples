/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-publishing-messages.html.

Purpose:
sns_publishtotopic.js demonstrates how to send a message to an Amazon SNS topic.

Inputs:
- REGION (into command line below)
- MESSAGE_TEXT (into code)
- TOPIC_ARN  (into command line below)

Running the code:
node sns_publishtotopic.js REGION TOPIC_ARN
 */
// snippet-start:[sns.JavaScript.v3.topics.publishMessages]
// Import required AWS SDK clients and commands for Node.js
const {SNS, PublishCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
var params = {
    Message: 'MESSAGE_TEXT', /* required */
    TopicArn: process.argv[3]
};

async function run() {
    try {
        const data = await sns.send(new PublishCommand(params));
        console.log("Message sent to the topic");
        console.log("MessageID is " + data.MessageId);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.v3.topics.publishMessages]
exports.run = run; //for unit tests only
