/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
cw_deletealarm.js demonstrates how to delete Amazon CloudWatch alarms.

Inputs:
- REGION
- ALARM_NAMES (e.g., Web_Server_CPU_Utilization)

Running the code:
node cw_deletealarm.js REGION ALARM_NAMES
*/
// snippet-start:[cw.JavaScript.v3.alarms.deleteAlarms]
// Import required AWS SDK clients and commands for Node.js
const {CloudWatch, DeleteAlarmsCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS Region
const region = process.argv[2];
// Create CloudWatch service object
const cw = new CloudWatch(region);
// Set the parameters
const params = {AlarmNames: [process.argv[3]]};

async function run() {
  try {
    const data = await cw.send(new DeleteAlarmsCommand(params));
    console.log("Success, alarm deleted; requestID:", data.$metadata.requestId);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.v3.alarms.deleteAlarms]
exports.run = run; //for unit tests only
