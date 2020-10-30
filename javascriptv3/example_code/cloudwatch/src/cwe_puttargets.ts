/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
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
  CloudWatchEventsClient,
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
const cwevents = new CloudWatchEventsClient(REGION);

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

