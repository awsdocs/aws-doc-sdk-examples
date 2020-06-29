/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-using-alarm-actions.html.

Purpose:
cw_disablealarmactions.js demonstrates how to disable actions for an Amazon CloudWatch alarm.
Inputs:
- REGION
- ALARM_NAME (e.g., Web_Server_CPU_Utilization)

Running the code:
node cw_disablealarmactions.js REGION ALARM_NAME
*/
// snippet-start:[cw.JavaScript.v3.alarms.disableAlarmActions]
// Import required AWS SDK clients and commands for Node.js
const {CloudWatch, DisableAlarmActionsCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS Region
const region = process.argv[2];
// Create CloudWatch service object
const cw = new CloudWatch(region);
// Set the parameters
var params = {AlarmNames: [process.argv[3]]};

async function run() {
  try {
    const data = await cw.send(new DisableAlarmActionsCommand(params));
    console.log("Success, alarm disabled:", data.$metadata.requestId);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.v3.alarms.disableAlarmActions]
exports.run = run; //for unit tests only
