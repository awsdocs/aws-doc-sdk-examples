/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_attachrolepolicy.js demonstrates how to attach a managed policy to an IAM role.

Inputs (into command line below):
- REGION
- ROLE_NAME

Running the code:
node iam_attachrolepolicy.js REGION ROLE_NAME

 */

// snippet-start:[iam.JavaScript.v3.policies.attachRolePolicy]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, ListAttachedRolePoliciesCommand, AttachRolePolicyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const paramsRoleList = {RoleName: process.argv[3]};

async function run() {
  const iam = new IAMClient(region);
  try {
    const data = await iam.send(new ListAttachedRolePoliciesCommand(paramsRoleList));
    const myRolePolicies = data.AttachedPolicies;
    myRolePolicies.forEach(function (val, index, array) {
      if (myRolePolicies[index].PolicyName === 'AmazonDynamoDBFullAccess') {
        console.log("AmazonDynamoDBFullAccess is already attached to this role.")
        process.exit();
      }
    });
    try {
      var params = {
        PolicyArn: 'arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess',
        RoleName: process.argv[2]
      };
      const data = await iam.send(new AttachRolePolicyCommand(params));
      console.log("Role attached successfully");
    }
    catch (err) {
      console.log('Error', err);
    }
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.policies.attachRolePolicy]
exports.run = run; //for unit tests only
