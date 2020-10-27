/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_describesubscriptionfilters.ts demonstrates how to list the subscription filters for an Amazon CloudWatch Logs group.

Inputs (replace in code): (all into command line below)
- REGION
- GROUP_NAME

Running the code:
ts-node cwl_describesubscriptionfilters.ts
*/
// snippet-start:[cwLogs.JavaScript.cwl.describeSubscriptionFiltersV3V3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchLogsClient,
  DescribeSubscriptionFiltersCommand,
} = require("@aws-sdk/client-cloudwatch-logs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  logGroupName: "GROUP_NAME", //GROUP_NAME
  limit: 5,
};

// Create CloudWatch service object
const cwl = new CloudWatchLogsClient(REGION);

const run = async () => {
  try {
    const data = await cwl.send(new DescribeSubscriptionFiltersCommand(params));
    console.log("Success", data.subscriptionFilters);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwLogs.JavaScript.cwl.describeSubscriptionFiltersV3V3]

