/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_putsubscriptionfilter.js demonstrates how to create or update a subscription filter for an Amazon CloudWatch Logs group.

Inputs: (all into command line below)
- REGION
- LAMBDA_FUNCTION_ARN
- FILTER_NAME
- LOG_GROUP

Running the code:
node cwl_putsubscriptionfilter.js REGION LAMBDA_FUNCTION_ARN FILTER_NAME LOG_GROUP
*/
// snippet-start:[cwLogs.JavaScript.v3.cwl.putSubscriptionFilter]
// Import required AWS-SDK clients and commands for Node.js
const {CloudWatchLogs, PutSubscriptionFilterCommand} = require("@aws-sdk/client-cloudwatch-logs");
// Set the AWS region
const region = process.argv[2];
// Create CloudWatch service object
const cwl = new CloudWatchLogs(region);
// Set the parameters
const params = {
  destinationArn: process.argv[3], //LAMBDA_FUNCTION_ARN
  filterName: process.argv[4], //FILTER_NAME
  filterPattern: 'ERROR',
  logGroupName: process.argv[5] //LOG_GROUP
};

async function run() {
  try {
    const data = await cwl.send(new PutSubscriptionFilterCommand(params));
    console.log("Success", data.subscriptionFilters);
  }
  catch(err){
    console.log("Error", err);
  }
};
// snippet-end:[cwLogs.JavaScript.v3.cwl.putSubscriptionFilter]
exports.run = run; //for unit tests only
