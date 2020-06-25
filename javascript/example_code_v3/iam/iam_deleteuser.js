/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_deleteuser.js demonstrates how to delete an IAM User from an AWS Account.

Inputs (into command line below):
- REGION
- USER_NAME

Running the code:
node iam_deleteuser.js REGION USER_NAME
 */
// snippet-start:[iam.JavaScript.v3.users.deleteUser]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, DeleteUserCommand, GetUserCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {UserName: process.argv[3]};

async function run() {
  try {
    const data = await iam.send(new GetUserCommand(params));
    try{
      const results = await iam.send(new DeleteUserCommand(params));
      console.log("Success", results);
    } catch(err){
      console.log('Error', err);
    }
  } catch (err) {
    console.log("User " + process.argv[2] + " does not exist.");
  }
};
run();
// snippet-end:[iam.JavaScript.v3.users.deleteUser]
exports.run = run; //for unit tests only
