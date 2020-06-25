/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//cloudwatch-examples-getting-metrics.html.

Purpose:
cw_listmetrics.js demonstrates how to list metrics for Amazon CloudWatch.

Inputs:
- REGION

Running the code:
node cw_listmetrics.js REGION
*/
// snippet-start:[cw.JavaScript.v3.metrics.listMetrics]
// Import required AWS-SDK clients and commands for Node.js
const {CloudWatch, ListMetricsCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS region
const region = process.argv[2];
// Create CloudWatch service object
const cw = new CloudWatch(region);
// Set the parameters
const params = {
  Dimensions: [
    {
      Name: 'LogGroupName', /* required */
    },
  ],
  MetricName: 'IncomingLogEvents',
  Namespace: 'AWS/Logs'
};

async function run() {
  try {
    const data = await cw.send(new ListMetricsCommand(params));
    console.log("Metrics", JSON.stringify(data.Metrics));
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.v3.metrics.listMetrics]
exports.run = run; //for unit tests only
