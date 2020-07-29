/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_putsubscriptionfilter.js demonstrates how to create or update a subscription filter for an Amazon CloudWatch Logs group.

Inputs (replace in code): (all into command line below)
- REGION
- LAMBDA_FUNCTION_ARN
- FILTER_NAME
- LOG_GROUP

Running the code:
node cwl_putsubscriptionfilter.js
*/
// snippet-start:[cwLogs.JavaScript.cwl.putSubscriptionFilterV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchLogs,
  PutSubscriptionFilterCommand,
} = require("@aws-sdk/client-cloudwatch-logs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  destinationArn: "LAMBDA_FUNCTION_ARN", //LAMBDA_FUNCTION_ARN
  filterName: "FILTER_NAME", //FILTER_NAME
  filterPattern: "ERROR",
  logGroupName: "LOG_GROUP", //LOG_GROUP
};

// Create CloudWatch service object
const cwl = new CloudWatchLogs(REGION);

const run = async () => {
  try {
    const data = await cwl.send(new PutSubscriptionFilterCommand(params));
    console.log("Success", data.subscriptionFilters);
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[cwLogs.JavaScript.cwl.putSubscriptionFilterV3]
exports.run = run; //for unit tests only
