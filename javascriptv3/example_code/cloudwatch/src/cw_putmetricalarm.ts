/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
cw_putmetricalarm.ts demonstrates how to create or update an Amazon CloudWatch alarm and associate it with particular metrics.

Inputs (replace in code):
- REGION

Running the code:
ts-node cw_putmetricalarm.ts
*/
// snippet-start:[cw.JavaScript.alarms.putMetricAlarmV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchClient,
  PutMetricAlarmCommand,
} = require("@aws-sdk/client-cloudwatch");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  AlarmName: "Web_Server_CPU_Utilization",
  ComparisonOperator: "GreaterThanThreshold",
  EvaluationPeriods: 1,
  MetricName: "CPUUtilization",
  Namespace: "AWS/EC2",
  Period: 60,
  Statistic: "Average",
  Threshold: 70.0,
  ActionsEnabled: false,
  AlarmDescription: "Alarm when server CPU exceeds 70%",
  Dimensions: [
    {
      Name: "InstanceId",
      Value: "INSTANCE_ID",
    },
  ],
  Unit: "Percent",
};

// Create CloudWatch service object
const cw = new CloudWatchClient({ region: REGION });

const run = async () => {
  try {
    const data = await cw.send(new PutMetricAlarmCommand(params));
    console.log(
      "Success",
      data.$metadata.requestId
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.alarms.putMetricAlarmV3]

