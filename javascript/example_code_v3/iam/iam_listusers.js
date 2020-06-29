/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_listusers.js demonstrates how to list IAM users.

Inputs (into command line below):
- REGION

Running the code:
node iam_listusers.js REGION
 */

// snippet-start:[iam.JavaScript.v3.users.listUsers]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, ListUsersCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {MaxItems: 10};

async function run() {
  try {
    const data = await iam.send(new ListUsersCommand(params));
    const users = data.Users || [];
    users.forEach(function(user) {
        console.log("User " + user.UserName + " created", user.CreateDate);
    });
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.users.listUsers]
exports.run = run; //for unit tests only
