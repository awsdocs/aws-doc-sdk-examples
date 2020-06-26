/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-subscribing-unubscribing-topics.html.

Purpose:
sns_unsubscribe.js demonstrates how to delete a subscription to an Amazon SNS topic.

Inputs:
- REGION (into command line below)
- TOPIC_SUBSCRIPTION_ARN (into command line below)

Running the code:
node sns_subscribeapp.js  REGION TOPIC_SUBSCRIPTION_ARN
 */
// snippet-start:[sns.JavaScript.v3.subscriptions.unsubscribe]
// Import required AWS SDK clients and commands for Node.js
const {SNS, UnsubscribeCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {SubscriptionArn : process.argv[3]};

async function run() {
    try {
        const data = await sns.send(new UnsubscribeCommand(params));
        console.log("Subscription is unsubscribed");
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.v3.subscriptions.unsubscribe]
exports.run = run; //for unit tests only
