/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_detachrolepolicy.js demonstrates how to detach a managed policy from an IAM role.

Inputs (into command line below):
- REGION
- ROLE_NAME

Running the code:
node iam_detachrolepolicy.js REGION ROLE_NAME
 */
// snippet-start:[iam.JavaScript.v3.policies.detachRolePolicy]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, ListAttachedRolePoliciesCommand, DetachRolePolicyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
var paramsRoleList = {RoleName: process.argv[3]};

async function run() {
  // Load the AWS SDK for Node.js
  // Create IAM service object
  try {
    const data = await iam.send(new ListAttachedRolePoliciesCommand(paramsRoleList));
    const myRolePolicies = data.AttachedPolicies;
    myRolePolicies.forEach(function (val, index, array) {
      if (myRolePolicies[index].PolicyName === 'AmazonDynamoDBFullAccess') {
        const params = {PolicyArn: 'arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess', paramsRoleList};
        try{
          const results = iam.send(new DetachRolePolicyCommand(paramsRoleList));
          console.log("Policy detached from role successfully");
          process.exit();
        } catch(err){
          console.log("Unable to detach policy from role", err);
        }
      }
      else{}
    });
  }
  catch (err) {
    console.log("User " + process.argv[2] + " does not exist.");
  }
};
run();
// snippet-end:[iam.JavaScript.v3.policies.detachRolePolicy]
exports.run = run; //for unit tests only
