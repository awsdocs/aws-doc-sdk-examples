/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-using-alarm-actions.html.

Purpose:
disableAlarmActions.js demonstrates how to disable actions for an Amazon CloudWatch alarm.

Inputs (replace in code):
- ALARM_NAME

Running the code:
node disableAlarmActions.js
*/
// snippet-start:[cw.JavaScript.alarms.disableAlarmActionsV3]

// Import required AWS SDK clients and commands for Node.js
import { DisableAlarmActionsCommand } from "@aws-sdk/client-cloudwatch";
import { cwClient } from "./libs/cloudWatchClient.js";

// Set the parameters
export const params = { AlarmNames: "ALARM_NAME" }; // e.g., "Web_Server_CPU_Utilization"

export const run = async () => {
  try {
    const data = await cwClient.send(new DisableAlarmActionsCommand(params));
    console.log("Success, alarm disabled:", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
// Uncomment this line to run execution within this file.
// run();
// snippet-end:[cw.JavaScript.alarms.disableAlarmActionsV3]

