/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-jobs.html.

Purpose:
emc_listjobs.ts demonstrates how to retrieve a list of the most-recently created transcoding jobs.

Inputs (replace in code):
- ACCOUNT_END_POINT
- QUEUE_ARN

Running the code:
ts-node emc_listjobs.ts
*/
// snippet-start:[mediaconvert.JavaScript.jobs.listJobsV3]

// Import required AWS-SDK clients and commands for Node.js
const {
  MediaConvertClient,
  ListJobsCommand,
} = require("@aws-sdk/client-mediaconvert");

// Set the parameters
const endpoint = { endpoint: "ACCOUNT_END_POINT" }; //ACCOUNT_END_POINT
var params = {
  MaxResults: 10,
  Order: "ASCENDING",
  Queue: "QUEUE_ARN",
  Status: "STATUS" // e.g., "SUBMITTED"
};

//Set the MediaConvert Service Object
const mediaconvert = new MediaConvert(endpoint);

const run = async () => {
  try {
    const data = await mediaconvert.send(new ListJobsCommand(params));
    console.log("Success. Jobs: ", data.Jobs);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.jobs.listJobsV3]
module.exports = {run};  //for unit tests only
