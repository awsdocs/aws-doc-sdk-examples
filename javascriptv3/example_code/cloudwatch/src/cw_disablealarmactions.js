/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-using-alarm-actions.html.

Purpose:
cw_disablealarmactions.js demonstrates how to disable actions for an Amazon CloudWatch alarm.

Inputs (replace in code):
- ALARM_NAME

Running the code:
node cw_disablealarmactions.js
*/
// snippet-start:[cw.JavaScript.alarms.disableAlarmActionsV3]

// Import required AWS SDK clients and commands for Node.js
const { DisableAlarmActionsCommand } = require("@aws-sdk/client-cloudwatch");
const { cwClient } = require("./libs/cwClient");

// Set the parameters
const params = { AlarmNames: "ALARM_NAME" }; // e.g., "Web_Server_CPU_Utilization"

const run = async () => {
  try {
    const data = await cwClient.send(new DisableAlarmActionsCommand(params));
    console.log("Success, alarm disabled:", data.$metadata.requestId);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.alarms.disableAlarmActionsV3]
// For unit tests only.
// module.exports ={run, params};
