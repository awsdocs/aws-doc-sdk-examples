/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_deleteuser.ts demonstrates how to delete an IAM user from an AWS account.

Inputs :
- REGION
- USER_NAME

Running the code:
ts-node iam_deleteuser.ts
 */
// snippet-start:[iam.JavaScript.users.deleteUserV3]

// Import required AWS SDK clients and commands for Node.js
const {
  IAMClient,
  DeleteUserCommand,
  GetUserCommand,
} = require ("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { UserName: "USER_NAME" }; //USER_NAME

// Create IAM service object
const iam = new IAMClient(REGION);

const run = async () => {
  try {
    const data = await iam.send(new GetUserCommand(params));
    try {
      const results = await iam.send(new DeleteUserCommand(params));
      console.log("Success", results);
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("User " + process.argv[2] + " does not exist.");
  }
};
run();
// snippet-end:[iam.JavaScript.users.deleteUserV3]
//for unit tests only
// module.exports = {run};
