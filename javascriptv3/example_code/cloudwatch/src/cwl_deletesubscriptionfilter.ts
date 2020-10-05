/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_deletesubscriptionfilter.ts demonstrates how to delete an Amazon CloudWatch Logs filter.

Inputs :
- REGION
- FILTER
- LOG_GROUP

Running the code:
ts-node cwl_deletesubscriptionfilter.ts
*/
// snippet-start:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchLogsClient,
  DeleteSubscriptionFilterCommand,
} = require("@aws-sdk/client-cloudwatch-logs");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  filterName: "FILTER", //FILTER
  logGroupName: "LOG_GROUP", //LOG_GROUP
};

// Create CloudWatch service object
const cwl = new CloudWatchLogsClient(REGION);

const run = async () => {
  try {
    const data = await cwl.send(new DeleteSubscriptionFilterCommand(params));
    console.log(
      "Success, subscription filter deleted; requestId: ",
      data.$metadata.requestId
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]

