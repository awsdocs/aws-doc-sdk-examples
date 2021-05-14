/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_describesubscriptionfilters.js demonstrates how to list the subscription filters for an Amazon CloudWatch Logs group.

Inputs (replace in code): (all into command line below)
- REGION
- GROUP_NAME

Running the code:
node cwl_describesubscriptionfilters.js
*/
// snippet-start:[cwLogs.JavaScript.cwl.describeSubscriptionFiltersV3]

// Import required AWS SDK clients and commands for Node.js
const {
  DescribeSubscriptionFiltersCommand,
} = require("@aws-sdk/client-cloudwatch-logs");
const { cwlClient } = require("./libs/cwlClient");

// Set the parameters
const params = {
  logGroupName: "GROUP_NAME", //GROUP_NAME
  limit: 5,
};

const run = async () => {
  try {
    const data = await cwlClient.send(
      new DescribeSubscriptionFiltersCommand(params)
    );
    console.log("Success", data.subscriptionFilters);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwLogs.JavaScript.cwl.describeSubscriptionFiltersV3]
// For unit tests only.
// module.exports ={run, params};
