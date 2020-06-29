/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK, 
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-managing-topics.html.

Purpose:
sns_createtopic.js demonstrates how to create an Amazom SNS topic to which notifications can be published.

Inputs:
- REGION (into command line below)
- TOPIC_NAME  (into command line below)

Running the code:
node sns_createtopic.js REGION PHONE_NUMBER
 */
// snippet-start:[sns.JavaScript.v3.topics.createTopic]

// Import required AWS SDK clients and commands for Node.js
const {SNS, CreateTopicCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {Name: process.argv[3]}; //TOPIC_NAME

async function run() {
    try {
        const data = await sns.send(new CreateTopicCommand(params));
        console.log("Topic ARN is " + data.TopicArn);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.v3.topics.createTopic]
exports.run = run; //for unit tests only
