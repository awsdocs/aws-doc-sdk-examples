/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-templates.html.

Purpose:
emc_deletetemplate.js demonstrates how to delete a transcoding job template.

Inputs: (all into command line below)
- ACCOUNT_END_POINT
- TEMPLATE_NAME

Running the code:
node emc_deletetemplate.js ACCOUNT_END_POINT TEMPLATE_NAME
*/
// snippet-start:[mediaconvert.JavaScript.templates.deleteJobTemplateV3]
// Import required AWS-SDK clients and commands for Node.js
const {MediaConvert, DeleteJobTemplateCommand} = require("@aws-sdk/client-mediaconvert");
// Create a new service object and set MediaConvert to customer endpoint
const endpoint = {endpoint: process.argv[2]}; //ACCOUNT_END_POINT
const mediaconvert = new MediaConvert(endpoint);
// Set the parameters
const params = {Name: process.argv[3]}; //TEMPLATE_NAME

async function run() {
    try {
        const data = await mediaconvert.send(new DeleteJobTemplateCommand(params));
        console.log("Success, template deleted! Request ID:", data.$metadata.requestId);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[mediaconvert.JavaScript.templates.deleteJobTemplateV3]
exports.run = run; //for unit tests only
