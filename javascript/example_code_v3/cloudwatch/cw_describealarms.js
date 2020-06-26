/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
cw_describealarms.js demonstrates how to retrieve information about Amazon CloudWatch alarms.

Inputs:
- REGION

Running the code:
node cw_describealarms.js REGION
*/
// snippet-start:[cw.JavaScript.v3.alarms.describeAlarms]
// Import required AWS SDK clients and commands for Node.js
const {CloudWatch, DescribeAlarmsCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS Region
const region = process.argv[2];
// Create CloudWatch service object
const cw = new CloudWatch(region);
// Set the parameters
var params = {StateValue: "INSUFFICIENT_DATA"};

async function run() {
  try {
    const data = await cw.send(new DescribeAlarmsCommand(params));
    console.log("Success", data);
    data.MetricAlarms.forEach(function (item, index, array) {
      console.log(item.AlarmName)});
      }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.v3.alarms.describeAlarms]
exports.run = run; //for unit tests only
