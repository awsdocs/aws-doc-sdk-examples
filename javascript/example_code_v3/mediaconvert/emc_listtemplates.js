/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-templates.html.

Purpose:
emc_listtemplates.js demonstrates how to retrieve transcoding job templates.

Inputs:
-

Running the code:
node emc_listtemplates.js ACCOUNT_END_POINT
*/
// snippet-start:[mediaconvert.JavaScript.v3.templates.listJobTemplates]

// Import required AWS-SDK clients and commands for Node.js
const {MediaConvert, ListJobTemplatesCommand} = require("@aws-sdk/client-mediaconvert");
// Create a new service object and set MediaConvert to customer endpoint
const endpoint = {endpoint : process.argv[2]};
const mediaconvert = new MediaConvert(endpoint);
// Set the parameters
var params = {
  ListBy: 'NAME',
  MaxResults: 10,
  Order: 'ASCENDING',
};

async function run(){
  try {
    const data = await mediaconvert.send(new ListJobTemplatesCommand(params));
    console.log("Success ", data.JobTemplates);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.v3.templates.listJobTemplates]
exports.run = run; //for unit tests only
