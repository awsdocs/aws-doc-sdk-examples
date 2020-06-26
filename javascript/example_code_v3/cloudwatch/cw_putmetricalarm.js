/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-creating-alarms.html.

Purpose:
cw_putmetricalarm.js demonstrates how to create or update an Amazon CloudWatch alarm and associate it with particular metrics.

Inputs:
- REGION

Running the code:
node cw_putmetricalarm.js REGION
*/
// snippet-start:[cw.JavaScript.v3.alarms.putMetricAlarm]
// Import required AWS SDK clients and commands for Node.js
const {CloudWatch, PutMetricAlarmCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS Region
const region = process.argv[2];
// Create CloudWatch service object
const cw = new CloudWatch(region);
// Set the parameters
const params = {
  AlarmName: 'Web_Server_CPU_Utilization',
  ComparisonOperator: 'GreaterThanThreshold',
  EvaluationPeriods: 1,
  MetricName: 'CPUUtilization',
  Namespace: 'AWS/EC2',
  Period: 60,
  Statistic: 'Average',
  Threshold: 70.0,
  ActionsEnabled: false,
  AlarmDescription: 'Alarm when server CPU exceeds 70%',
  Dimensions: [
    {
      Name: 'InstanceId',
      Value: 'INSTANCE_ID'
    },
  ],
  Unit: 'Percent'
};

async function run() {
  try {
    const data = await cw.send(new PutMetricAlarmCommand(params));
    console.log("Success, action enabled on alarm; requestID:", data.$metadata.requestId);  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.v3.alarms.putMetricAlarm]
exports.run = run; //for unit tests only
