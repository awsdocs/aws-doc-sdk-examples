/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-jobs.html.

Purpose:
emc_canceljob.js demonstrates how to remove a transcoding job from the queue.

Inputs: (all into command line below)
- REGION
- ACCOUNT_ENDPOINT
- JOB_ID

Running the code:
node emc_canceljob.js ACCOUNT_ENDPOINT JOB_ID
*/
// snippet-start:[mediaconvert.JavaScript.v3.jobs.cancelJob]
// Import required AWS-SDK clients and commands for Node.js
const {MediaConvert, CancelJobCommand} = require("@aws-sdk/client-mediaconvert");
// Create MediaConvert service object
const endpoint = {endpoint : process.argv[2]}; //ACCOUNT_ENDPOINT
const mediaconvert = new MediaConvert(endpoint);
// Set the parameters
const params = {Id: process.argv[3]}; //JOB_ID

async function run(){
    try {
        const data = await mediaconvert.send(new CancelJobCommand(params));
        console.log("Job  " + params.Id + " is canceled");
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[mediaconvert.JavaScript.v3.jobs.cancelJob]
exports.run = run; //for unit tests only
