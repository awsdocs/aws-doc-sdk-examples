/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_putrule.js demonstrates how to create or update an Amazon CloudWatch Events rule.

Inputs (replace in code):
- REGION IAM_ROLE_ARN

Running the code:
node cw_deletealarm.js
*/
// snippet-start:[cwEvents.JavaScript.cwe.putRuleV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchEvents,
  PutRuleCommand,
} = require("@aws-sdk/client-cloudwatch-events");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Name: "DEMO_EVENT",
  RoleArn: "IAM_ROLE_ARN", //IAM_ROLE_ARN
  ScheduleExpression: "rate(5 minutes)",
  State: "ENABLED",
};

// Create CloudWatch service object
const cwevents = new CloudWatchEvents(REGION);

const run = async () => {
  try {
    const data = await cwevents.send(new PutRuleCommand(params));
    console.log("Success, scheduled rule created; Rule ARN:", data.RuleArn);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwEvents.JavaScript.cwe.putRuleV3]
exports.run = run; //for unit tests only
