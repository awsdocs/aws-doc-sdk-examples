/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-using-alarm-actions.html.

Purpose:
cw_enablealarmactions.js demonstrates how to enable actions for Amazon CloudWatch alarms.

Inputs:
- REGION (into command line below)
- ACTION_ARN (into command line below)

Running the code:
node cw_enablealarmactions.js REGION ACTION_ARN
*/
// snippet-start:[cw.JavaScript.v3.alarms.enableAlarmActions]
// Import required AWS-SDK clients and commands for Node.js
const {CloudWatch, PutMetricAlarmCommand,
  EnableAlarmActionsCommand} = require("@aws-sdk/client-cloudwatch");
// Set the AWS region
const region = process.argv[2];
// Create CloudWatch service object
const cw = new CloudWatch(region);
// Set the parameters
const params = {
  AlarmName:  process.argv[3],
  ComparisonOperator: 'GreaterThanThreshold',
  EvaluationPeriods: 1,
  MetricName: 'CPUUtilization',
  Namespace: 'AWS/EC2',
  Period: 60,
  Statistic: 'Average',
  Threshold: 70.0,
  ActionsEnabled: true,
  AlarmActions: ['arn:aws:automate:us-east-1:ec2:stop'],
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
  try{
    const data = await cw.send(new PutMetricAlarmCommand(params));
    console.log("Alarm action added; RequestID:", data.$metadata.requestId);
    var paramsEnableAlarmAction = {
      AlarmNames: [params.AlarmName]
    };
    try {
      const data = await cw.send(new EnableAlarmActionsCommand(paramsEnableAlarmAction));
      console.log("Alarm action enabled; RequestID:", data.$metadata.requestId);
    }
    catch(err){
      console.log("Error", err);
    }
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cw.JavaScript.v3.alarms.enableAlarmActions]
exports.run = run;
