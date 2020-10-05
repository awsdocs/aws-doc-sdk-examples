/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-jobs.html.

Purpose:
emc_canceljob.ts demonstrates how to remove a transcoding job from the queue.

Inputs (replace in code): (all into command line below)
- ACCOUNT_ENDPOINT
- JOB_ID

Running the code:
ts-node emc_canceljob.ts
*/
// snippet-start:[mediaconvert.JavaScript.jobs.cancelJobV3]
// Import required AWS-SDK clients and commands for Node.js
const {
  MediaConvertClient,
  CancelJobCommand
} = require("@aws-sdk/client-mediaconvert");

// Set the parameters
const endpoint = { endpoint: "ACCOUNT_ENDPOINT" }; //ACCOUNT_ENDPOINT
const params = { Id: "JOB_ID" }; //JOB_ID

// Create MediaConvert service object
const mediaconvert = new MediaConvertClient(endpoint);

const run = async () => {
  try {
    const data = await mediaconvert.send(new CancelJobCommand(params));
    console.log("Job  " + params.Id + " is canceled");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.jobs.cancelJobV3]

