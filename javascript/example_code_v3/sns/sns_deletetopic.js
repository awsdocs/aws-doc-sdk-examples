/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-managing-topics.html.

Purpose:
sns_deletetopic.js demonstrates how to delete an Amazon SNS topic and all its subscriptions.

Inputs (replace in code):
- REGION
- TOPIC_ARN

Running the code:
node sns_deletetopic.js
 */
// snippet-start:[sns.JavaScript.topics.deleteTopicV3]
// Load the AWS SDK for Node.js

// Import required AWS SDK clients and commands for Node.js
const {SNS, DeleteTopicCommand} = require("@aws-sdk/client-sns");

// Create SNS service object
const sns = new SNS(REGION);

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {TopicArn: "TOPIC_ARN"}; //TOPIC_ARN

const run = async () => {
    try {
        const data = await sns.send(new DeleteTopicCommand(params));
        console.log("Topic Deleted");
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.topics.deleteTopicV3]
exports.run = run; //for unit tests only
