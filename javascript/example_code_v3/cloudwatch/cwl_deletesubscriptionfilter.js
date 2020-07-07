/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_deletesubscriptionfilter.js demonstrates how to delete an Amazon CloudWatch Logs filter.

Inputs (into command line below):
- REGION
- FILTER
- LOG_GROUP

Running the code:
node cwl_deletesubscriptionfilter.js REGION FILTER LOG_GROUP
*/
// snippet-start:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]
// Import required AWS SDK clients and commands for Node.js
const {CloudWatchLogs, DeleteSubscriptionFilterCommand} = require("@aws-sdk/client-cloudwatch-logs");
// Set the AWS Region
const region = process.argv[2];
// Create CloudWatch service object
const cwl = new CloudWatchLogs(region);
// Set the parameters
const params = {
  filterName: process.argv[3],
  logGroupName: process.argv[4]
};

async function run() {
  try {
    const data = await cwl.send(new DeleteSubscriptionFilterCommand(params));
    console.log("Success, subscription filter deleted; requestId: ", data.$metadata.requestId);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]
exports.run = run; //for unit tests only
