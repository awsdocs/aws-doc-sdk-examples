/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK, 
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-subscribing-unubscribing-topics.html.

Purpose:
sns_subscribelambda.test.js demonstrates how to initiate a subscription to an Amazon SNS topic with
delivery to an AWS Lambda function.

Inputs:
- REGION (into command line below)
- TOPIC_ARN (into command line below)
- LAMBDA_FUNCTION_ARN (into command line below)

Running the code:
node sns_subscribelambda.js  REGION TOPIC_ARN LAMBDA_FUNCTION_ARN
 */
// snippet-start:[sns.JavaScript.v3.subscriptions.subscribeLambda]
// Import required AWS SDK clients and commands for Node.js
const {SNS, SubscribeCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {
  Protocol: "lambda", /* required */
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
// snippet-end:[sns.JavaScript.v3.subscriptions.subscribeLambda]
exports.run = run; //for unit tests only
