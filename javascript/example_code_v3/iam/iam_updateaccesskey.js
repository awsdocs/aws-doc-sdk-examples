/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_updateaccesskey.js demonstrates how to update the status of an IAM user's Access Key.

Inputs (into command line below):
- REGION
- ACCESS_KEY_ID
- USER_NAME

Running the code:
node iam_updateaccesskey.js REGION ACCESS_KEY_ID USER_NAME
 */

// snippet-start:[iam.JavaScript.v3.keys.updateAccessKey]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, UpdateAccessKeyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
var params = {
  AccessKeyId: process.argv[3],
  Status: 'Active',
  UserName: process.argv[4]
};

async function run() {
   try {
    const data = await iam.send(new UpdateAccessKeyCommand(params));
    console.log("Success", data);
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.keys.updateAccessKey]
exports.run = run; //for unit tests only
