/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_puttargets.ts demonstrates how to add or update a target to an Amazon CloudWatch Events rule.

Inputs (replace in code):
- REGION
- LAMBDA_FUNCTION_ARN

Running the code:
ts-node cwe_puttargets.ts
*/
// snippet-start:[cwEvents.JavaScript.cwe.putTargetsV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchEvents,
  PutTargetsCommand,
} = require("@aws-sdk/client-cloudwatch-events");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Rule: "DEMO_EVENT",
  Targets: [
    {
      Arn: "LAMBDA_FUNCTION_ARN", //LAMBDA_FUNCTION_ARN
      Id: "myCloudWatchEventsTarget",
    },
  ],
};

// Create CloudWatch service object
const cwevents = new CloudWatchEvents(REGION);

const run = async () => {
  try {
    const data = await cwevents.send(new PutTargetsCommand(params));
    console.log("Success, target added; requestID: ", data.$metadata.requestId);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwEvents.JavaScript.cwe.putTargetsV3]
//for unit tests only
export = {run};
