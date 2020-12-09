/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_putrule.ts demonstrates how to create or update an Amazon CloudWatch Events rule.

Inputs (replace in code):
- REGION IAM_ROLE_ARN

Running the code:
ts-node cw_deletealarm.ts
*/
// snippet-start:[cwEvents.JavaScript.cwe.putRuleV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchEventsClient,
  PutRuleCommand,
} = require("@aws-sdk/client-cloudwatch-events");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Name: "DEMO_EVENT",
  RoleArn: "IAM_ROLE_ARN", //IAM_ROLE_ARN
  ScheduleExpression: "rate(5 minutes)",
  State: "ENABLED",
};

// Create CloudWatch service object
const cwevents = new CloudWatchEventsClient({ region: REGION });

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

