/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_puttargets.js demonstrates how to add or update a target to an Amazon CloudWatch Events rule.

Inputs:
- REGION
- LAMBDA_FUNCTION_ARN
- myCloudWatchEventsTarget

Running the code:
node cwe_puttargets.js REGION LAMBDA_FUNCTION_ARN
*/
// snippet-start:[cwEvents.JavaScript.v3.cwe.putTargets]
// Import required AWS SDK clients and commands for Node.js
const {CloudWatchEvents, PutTargetsCommand} = require("@aws-sdk/client-cloudwatch-events");
// Set the AWS Region
const region = process.argv[2];
// Create CloudWatch service object
const cwevents = new CloudWatchEvents(region);
// Set the parameters
const params = {
  Rule: 'DEMO_EVENT',
  Targets: [
    {
      Arn: process.argv[3], //LAMBDA_FUNCTION_ARN
      Id: "myCloudWatchEventsTarget"
    }
  ]
};

async function run() {
  try {
    const data = await cwevents.send(new PutTargetsCommand(params));
    console.log("Success, target added; requestID: ", data.$metadata.requestId);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwEvents.JavaScript.v3.cwe.putTargets]
exports.run = run; //for unit tests only
