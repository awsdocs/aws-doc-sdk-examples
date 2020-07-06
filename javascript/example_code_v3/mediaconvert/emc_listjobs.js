/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-jobs.html.

Purpose:
emc_listjobs.js demonstrates how to retrieve a list of the most-recently created transcoding jobs.

Inputs: (all into command line below)
- ACCOUNT_END_POINT
- QUEUE_ARN

Running the code:
node emc_listjobs.js ACCOUNT_END_POINT QUEUE_ARN
*/
// snippet-start:[mediaconvert.JavaScript.v3.jobs.listJobs]

// Import required AWS-SDK clients and commands for Node.js
const {MediaConvertClient, ListJobsCommand} = require("@aws-sdk/client-mediaconvert");
// Create a new service object and set MediaConvert to customer endpoint
const endpoint = {endpoint: process.argv[2]}; //ACCOUNT_END_POINT
const mediaconvert = new MediaConvertClient(endpoint);
var params = {
  MaxResults: 10,
  Order: 'ASCENDING',
  Queue: process.argv[3],
  Status: 'SUBMITTED'
};

async function run(){
  try {
    const data = await mediaconvert.send(new ListJobsCommand(params));
    console.log("Success. Jobs: ", data.Jobs);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.v3.jobs.listJobs]
exports.run = run; //for unit tests only
