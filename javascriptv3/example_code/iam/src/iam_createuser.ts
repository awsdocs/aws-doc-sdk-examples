/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_createuser.js demonstrates how to create an IAM user for an AWS account.

Inputs :
- REGION
- USER_NAME

Running the code:
ts-node iam_createuser.js
 */

// snippet-start:[iam.JavaScript.users.getUserV3]
// Import required AWS SDK clients and commands for Node.js
const {
  IAMClient,
  GetUserCommand,
  CreateUserCommand
} = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { UserName: "USER_NAME" }; //USER_NAME

// Create IAM service object
const iam = new IAMClient(REGION);

const run = async () => {
  try {
    const data = await iam.send(new GetUserCommand(params));
    console.log(
      "User " + process.argv[3] + " already exists",
      data.User.UserId
    );
  } catch (err) {
    try {
      const results = await iam.send(new CreateUserCommand(params));
      console.log("Success", results);
    } catch (err) {
      console.log("Error", err);
    }
  }
};
run();
// snippet-end:[iam.JavaScript.users.getUserV3]

