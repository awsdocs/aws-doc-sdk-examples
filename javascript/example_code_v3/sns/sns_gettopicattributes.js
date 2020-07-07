/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-managing-topics.html.

Purpose:
sns_gettopicattributes.js demonstrates how to retrieve the properties of an Amazon SNS topic.

Inputs:
- REGION (into command line below)
- TOPIC_ARN (into command line below)

Running the code:
node sns_gettopicattributes.js REGION TOPIC_ARN
*/
// snippet-start:[sns.JavaScript.topics.getTopicAttributesV3]

// Import required AWS SDK clients and commands for Node.js
const {SNS, GetTopicAttributesCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {TopicArn: process.argv[3]};

async function run() {
    try {
        const data = await sns.send(new GetTopicAttributesCommand(params));
        console.log("Success. Attributes:", data.Attributes);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.topics.getTopicAttributesV3]
exports.run = run; //for unit tests only
