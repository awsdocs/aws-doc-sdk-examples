/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_listaccesskeys.js demonstrates how to retrieve information about the access keys of an IAM User.

Inputs (into command line below):
- REGION
- IAM_USER_NAME

Running the code:
node iam_listaccesskeys.js REGION IAM_USER_NAME
 */

// snippet-start:[iam.JavaScript.v3.keys.listAccessKeys]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, ListAccessKeysCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {
  MaxItems: 5,
  UserName: process.argv[3]
};

async function run() {
  try {
    const data = await iam.send(new ListAccessKeysCommand(params));
    console.log("Success", data);
  }
  catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.keys.listAccessKeys]
exports.run = run; //for unit tests only
