/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-getendpoint.html.

Purpose:
emc_getendpoint.js demonstrates how to retrieve information about the endpoints for an AWS account.

Inputs: (all into command line below)
- ACCOUNT_END_POINT

Running the code:
node emc_getendpoint.js ACCOUNT_END_POINT
*/

// snippet-start:[mediaconvert.JavaScript.v3.endoint.describeEndpoints]
// Import required AWS-SDK clients and commands for Node.js
const {MediaConvertClient, DescribeEndpointsCommand} = require("@aws-sdk/client-mediaconvert");
const endpoint = {endpoint: process.argv[2]}; //ACCOUNT_END_POINT
// Create a new MediaConvertClient object and set MediaConvert to customer endpoint
const mediaconvert = new MediaConvertClient(endpoint);
//set the parameters
const params = {MaxResults: 0};

async function run() {
    try {
        // Load the required SDK for JavaScript modules
        // Create a new service object and set MediaConvert to customer endpoint
        const params = {MaxResults: 0};
        const data = await mediaconvert.send(new DescribeEndpointsCommand(params));
        console.log("Your MediaConvert endpoint is ", data.Endpoints);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[mediaconvert.JavaScript.v3.endoint.describeEndpoints]

exports.run = run; //for unit tests only
