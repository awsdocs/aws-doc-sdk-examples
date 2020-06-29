/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-account-aliases.html.

Purpose:
iam_createaccountalias.js demonstrates how to create an alias for an AWS account.

Inputs (into command line below):
- REGION
- ACCOUNT_ALIAS

Running the code:
node iam_createaccountalias.js REGION ACCOUNT_ALIAS
 */
// snippet-start:[iam.JavaScript.v3.alias.createAccountAlias]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, CreateAccountAliasCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const accountAlias = {AccountAlias: process.argv[3]};

async function run() {
  try {
    const data = await iam.send(new CreateAccountAliasCommand(accountAlias));
    console.log("Success", data);
  } catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.alias.createAccountAlias]
exports.run = run; //for unit tests only
