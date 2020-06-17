/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/iam-examples-account-aliases.html

Purpose:
iam_deleteaccountalias.test.js demonstrates how to delete an alias for an AWS account.

Inputs (in command line below):
- REGION
- ALIAS

Running the code:
node iam_createaccountalias.js REGION ALIAS
 */
// snippet-start:[iam.JavaScript.alias.deleteAccountAlias]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, DeleteAccountAliasCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  const params = {AccountAlias: process.argv[3]}
  try {
    const data = await iam.send(new DeleteAccountAliasCommand(params));
    console.log("Success", data);
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[iam.JavaScript.alias.deleteAccountAlias]
exports.run = run;
