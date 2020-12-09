/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-subscribing-unubscribing-topics.html.

Purpose:
sns_listsubscriptions.ts demonstrates how to retrieve a list of Amazon SNS subscriptions.

Inputs (replace in code):
- REGION
- TOPIC_ARN

Running the code:
ts-node sns_listsubscriptions.ts
*/
// snippet-start:[sns.JavaScript.subscriptions.listSubscriptionsByTopicV3]
// Import required AWS SDK clients and commands for Node.js
const { SNSClient, ListSubscriptionsByTopicCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { TopicArn: "TOPIC_ARN" }; //TOPIC_ARN

//Create SNS service object
const sns = new SNSClient({ region: REGION });

const run = async () => {
  try {
    const data = await sns.send(new ListSubscriptionsByTopicCommand(params));
    console.log("Success. Subscriptions:", data.Subscriptions);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.subscriptions.listSubscriptionsByTopicV3]

