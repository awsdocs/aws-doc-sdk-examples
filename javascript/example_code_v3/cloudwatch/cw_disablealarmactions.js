/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-using-alarm-actions.html .

Purpose:
cw_disablealarmactions.js demonstrates how to disable actions for an Amazon CloudWatch alarm.
Inputs:
- REGION
- ALARM_NAME (e.g. Web_Server_CPU_Utilization)

Running the code:
node cw_disablealarmactions.js REGION ALARM_NAME
*/
// snippet-start:[cw.JavaScript.v3.alarms.disableAlarmActions]
// Import required AWS-SDK clients and commands for Node.js
const {CloudWatch, DisableAlarmActionsCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS region
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
