/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-publishing-messages.html.

Purpose:
sns_publishtotopic.ts demonstrates how to send a message to an Amazon SNS topic.

Inputs (replace in code):
- REGION
- MESSAGE_TEXT
- TOPIC_ARN

Running the code:
ts-node sns_publishtotopic.ts
 */
// snippet-start:[sns.JavaScript.topics.publishMessagesV3]
// Import required AWS SDK clients and commands for Node.js
const { SNSClient, PublishCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
var params = {
  Message: "MESSAGE_TEXT", // MESSAGE_TEXT
  TopicArn: "TOPIC_ARN", //TOPIC_ARN
};

// Create SNS service object
const sns = new SNSClient(REGION);

const run = async () => {
  try {
    const data = await sns.send(new PublishCommand(params));
    console.log("Message sent to the topic");
    console.log("MessageID is " + data.MessageId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.topics.publishMessagesV3]

