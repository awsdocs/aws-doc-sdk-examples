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
iam_updateuser.js demonstrates how to update the name of an IAM user.

Inputs (in command line below):
- REGION
- ORIGINGAL_USER_NAME
- NEW_USER_NAME

Running the code:
node iam_updateuser.js REGION ORIGINGAL_USER_NAME NEW_USER_NAME
 */
// snippet-start:[iam.JavaScript.users.updateUser]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, UpdateUserCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  const params = {
    UserName: process.argv[3],
    NewUserName: process.argv[4]
  };
  try {
    const data = await iam.send(new UpdateUserCommand(params));
    console.log("Success", data);
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.users.updateUser]
exports.run = run;
