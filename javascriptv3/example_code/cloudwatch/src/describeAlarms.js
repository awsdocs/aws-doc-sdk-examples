/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
describeAlarms.js demonstrates how to retrieve information about Amazon CloudWatch alarms.

Running the code:
node describeAlarms.js
*/
// snippet-start:[cw.JavaScript.alarms.describeAlarmsV3]

// Import required AWS SDK clients and commands for Node.js
import { DescribeAlarmsCommand } from "@aws-sdk/client-cloudwatch";
import { cwClient } from "./libs/cloudWatchClient.js";

// Set the parameters
export const params = { StateValue: "INSUFFICIENT_DATA" };

export const run = async () => {
  try {
    const data = await cwClient.send(new DescribeAlarmsCommand(params));
    console.log("Success", data);
    return data;
    data.MetricAlarms.forEach(function (item, index, array) {
      console.log(item.AlarmName);
      return data;
    });
  } catch (err) {
    console.log("Error", err);
  }
};
// Uncomment this line to run execution within this file.
// run();
// snippet-end:[cw.JavaScript.alarms.describeAlarmsV3]

