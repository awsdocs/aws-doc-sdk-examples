/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_accesskeylastused.test.js demonstrates how to retrieve information about the last time an IAM access key was used.

Inputs (into command line below):
- REGION
- ACCESS_KEY_ID

Running the code:
node iam_accesskeylastused.js REGION ACCESS_KEY_ID

 */
// snippet-start:[iam.JavaScript.v3.keys.getAccessKeyLastUsed]
// Import required AWS-SDK clients and commands for Node.js
const {IAMClient, GetAccessKeyLastUsedCommand} = require("@aws-sdk/client-iam");
// Set the AWS region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {AccessKeyId: process.argv[3]};

async function run() {
  try {
    const data = await iam.send(new GetAccessKeyLastUsedCommand(params));
    console.log('Success', data.AccessKeyLastUsed);
      }
  catch (err) {
    console.log('Error', err);
      }
};
run();
// snippet-end:[iam.JavaScript.v3.keys.getAccessKeyLastUsed]
exports.run = run; //for unit tests only
