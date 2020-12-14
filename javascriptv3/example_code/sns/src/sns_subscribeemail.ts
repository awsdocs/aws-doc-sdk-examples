/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-subscribing-unubscribing-topics.html.

Purpose:
sns_subscribeemail.ts demonstrates how to initiate a subscription to an Amazon SNS topic with delivery to an email address.]

Inputs (replace in code):
- REGION
- TOPIC_ARN
- EMAIL_ADDRESS

Running the code:
ts-node sns_subscribeapp.ts
 */
// snippet-start:[sns.JavaScript.subscriptions.subscribeEmailV3]

// Import required AWS SDK clients and commands for Node.js
const { SNSClient, SubscribeCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Protocol: "email" /* required */,
  TopicArn: "TOPIC_ARN", //TOPIC_ARN
  Endpoint: "EMAIL_ADDRESS", //EMAIL_ADDRESS
};

// Create SNS service object
const sns = new SNSClient({ region: REGION });

const run = async () => {
  try {
    const data = await sns.send(new SubscribeCommand(params));
    console.log("Subscription ARN is " + data.SubscriptionArn);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.subscriptions.subscribeEmailV3]

