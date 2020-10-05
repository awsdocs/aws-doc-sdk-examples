/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
cw_describealarms.ts demonstrates how to retrieve information about Amazon CloudWatch alarms.

Inputs (replace in code):
- REGION

Running the code:
ts-node cw_describealarms.ts
*/
// snippet-start:[cw.JavaScript.alarms.describeAlarmsV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchClient,
  DescribeAlarmsCommand
} = require("@aws-sdk/client-cloudwatch");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { StateValue: "INSUFFICIENT_DATA" };

// Create CloudWatch service object
const cw = new CloudWatchClient(REGION);

const run = async () => {
  try {
    const data = await cw.send(new DescribeAlarmsCommand(params));
    console.log("Success", data);
    data.MetricAlarms.forEach(function (item, index, array) {
      console.log(item.AlarmName);
    });
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.alarms.describeAlarmsV3]

