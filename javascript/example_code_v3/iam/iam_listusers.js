/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/iam-examples-managing-users.html

Purpose:
iam_listusers.js demonstrates how to list IAM users.Inputs:

Inputs (in command line below):
- REGION

Running the code:
node iam_listusers.js REGION
 */

// snippet-start:[iam.JavaScript.users.listUsers]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, ListAccountAliasesCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  const params = {
    MaxItems: 10
  };
  try {
    const data = await iam.send(new ListAccountAliasesCommand(params));
    const users = data.Users || [];
    users.forEach(function(user) {
      console.log("User " + user.UserName + " created", user.CreateDate);
    });
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.users.listUsers]
exports.run = run;
