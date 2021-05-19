/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//cloudwatch-examples-getting-metrics.html.

Purpose:
cw_listmetrics.js demonstrates how to list metrics for Amazon CloudWatch.


Running the code:
node cw_listmetrics.js
*/
// snippet-start:[cw.JavaScript.metrics.listMetricsV3]

// Import required AWS SDK clients and commands for Node.js
const { ListMetricsCommand } = require("@aws-sdk/client-cloudwatch");
const { cwClient } = require("./libs/cwClient");

// Set the parameters
const params = {
  Dimensions: [
    {
      Name: "LogGroupName" /* required */,
    },
  ],
  MetricName: "IncomingLogEvents",
  Namespace: "AWS/Logs",
};

const run = async () => {
  try {
    const data = await cwClient.send(new ListMetricsCommand(params));
    console.log("Success. Metrics:", JSON.stringify(data.Metrics));
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.metrics.listMetricsV3]
// For unit tests only.
// module.exports ={run, params};
