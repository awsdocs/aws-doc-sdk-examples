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
iam_createuser.test.js demonstrates how to create an IAM user for an AWS account.

Inputs (in command line below):
- REGION
- USER_NAME

Running the code:
node iam_createuser.js REGION USER_NAME
 */

// snippet-start:[iam.JavaScript.users.getUser]
// Load the AWS SDK for Node.js
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, GetUserCommand, CreateUserCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  const params = {UserName: process.argv[3]};
  try {
    const data = await iam.send(new GetUserCommand(params));
    console.log("User " + process.argv[3] + " already exists", data.User.UserId);
  } catch (err) {
    try {
      const results = await iam.send(new CreateUserCommand(params));
      console.log("Success", results);
    } catch (err) {
      console.log('Error', err);
    }
  }
};
run();
// snippet-end:[iam.JavaScript.users.getUser]
exports.run = run;
