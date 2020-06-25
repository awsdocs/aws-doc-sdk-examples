/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_updateuser.js demonstrates how to update the name of an IAM User.

Inputs (into command line below):
- REGION
- ORIGINGAL_USER_NAME
- NEW_USER_NAME

Running the code:
node iam_updateuser.js REGION ORIGINGAL_USER_NAME NEW_USER_NAME
 */
// snippet-start:[iam.JavaScript.v3.users.updateUser]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, UpdateUserCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {
  UserName: process.argv[3],
  NewUserName: process.argv[4]
};

async function run() {
  try {
    const data = await iam.send(new UpdateUserCommand(params));
    console.log("Success, username updated");
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.users.updateUser]
exports.run = run; //for unit tests only
