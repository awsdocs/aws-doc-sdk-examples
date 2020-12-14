/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_updateuser.js demonstrates how to update the name of an IAM user.

Inputs :
- REGION
- ORIGINAL_USER_NAME
- NEW_USER_NAME

Running the code:
ts-node iam_updateuser.ts
 */
// snippet-start:[iam.JavaScript.users.updateUserV3]

// Import required AWS SDK clients and commands for Node.js
const { IAMClient, UpdateUserCommand } = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  UserName: "ORIGINAL_USER_NAME", //ORIGINAL_USER_NAME
  NewUserName: "NEW_USER_NAME", //NEW_USER_NAME
};

// Create IAM service object
const iam = new IAMClient({ region: REGION });

const run = async () => {
  try {
    const data = await iam.send(new UpdateUserCommand(params));
    console.log("Success, username updated");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.users.updateUserV3]

