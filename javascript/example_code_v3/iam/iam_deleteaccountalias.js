/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-account-aliases.html.

Purpose:
iam_deleteaccountalias.js demonstrates how to delete an alias for an AWS Account.

Inputs (into command line below):
- REGION
- ALIAS

Running the code:
node iam_createaccountalias.js REGION ALIAS
 */
// snippet-start:[iam.JavaScript.v3.alias.deleteAccountAlias]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, DeleteAccountAliasCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {AccountAlias: process.argv[3]}

async function run() {
  // Load the AWS SDK for Node.js
  // Create IAM service object
  try {
    const data = await iam.send(new DeleteAccountAliasCommand(params));
    console.log("Success", data);
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.alias.deleteAccountAlias]
exports.run = run; //for unit tests only
