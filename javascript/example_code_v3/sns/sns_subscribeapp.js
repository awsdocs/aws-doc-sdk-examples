/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-subscribing-unubscribing-topics.html.

Purpose:
sns_subscribeapp.js demonstrates how to initiate a subscription to an Amazon SNS topic with delivery to a mobile app.

Inputs:
- REGION (into command line below)
- TOPIC_ARN (into command line below)
- MOBILE_ENDPOINT_ARN (into command line below)

Running the code:
node sns_subscribeapp.js  REGION TOPIC_ARN MOBILE_ENDPOINT_ARN
 */
// snippet-start:[sns.JavaScript.subscriptions.subscribeAppV3]
// Import required AWS SDK clients and commands for Node.js
const {SNS, SubscribeCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {
  Protocol: "application", /* required */
  TopicArn: process.argv[3], /* required */
  Endpoint: process.argv[4]
};

async function run() {
  try {
    const data = await sns.send(new SubscribeCommand(params));
    console.log("Subscription ARN is " + data.SubscriptionArn);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.subscriptions.subscribeAppV3]
exports.run = run; //for unit tests only
