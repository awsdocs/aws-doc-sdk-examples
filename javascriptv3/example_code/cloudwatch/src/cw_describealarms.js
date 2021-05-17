/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
cw_describealarms.js demonstrates how to retrieve information about Amazon CloudWatch alarms.

Inputs (replace in code):
- REGION

Running the code:
node cw_describealarms.js
*/
// snippet-start:[cw.JavaScript.alarms.describeAlarmsV3]

// Import required AWS SDK clients and commands for Node.js
const { DescribeAlarmsCommand } = require("@aws-sdk/client-cloudwatch");
const { cwClient } = require("./libs/cwClient");

// Set the parameters
const params = { StateValue: "INSUFFICIENT_DATA" };

const run = async () => {
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
run();
// snippet-end:[cw.JavaScript.alarms.describeAlarmsV3]
// For unit tests only.
// module.exports ={run, params};
