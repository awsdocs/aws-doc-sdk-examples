/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-account-aliases.html.

Purpose:
iam_listaccountaliases.js demonstrates how to retrieve information about the aliases for an AWS Account.

Inputs (into command line below):
- REGION

Running the code:
node iam_listaccountaliases.js REGION
 */

// snippet-start:[iam.JavaScript.v3.alias.listAccountAliases]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, ListAccountAliasesCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {MaxItems: 5};

async function run() {
  try {
    const data = await iam.send(new ListAccountAliasesCommand(params));
    console.log("Success", data);
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.alias.listAccountAliases]
exports.run = run; //for unit tests only
