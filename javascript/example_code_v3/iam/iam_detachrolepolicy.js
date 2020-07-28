/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_detachrolepolicy.js demonstrates how to detach a managed policy from an IAM role.

Inputs :
- REGION
- ROLE_NAME

Running the code:
node iam_detachrolepolicy.js
 */
// snippet-start:[iam.JavaScript.policies.detachRolePolicyV3]

// Import required AWS SDK clients and commands for Node.js
const {
  IAMClient,
  ListAttachedRolePoliciesCommand,
  DetachRolePolicyCommand,
} = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
var paramsRoleList = { RoleName: "ROLE_NAME" }; //ROLE_NAME

// Create IAM service object
const iam = new IAMClient(REGION);

const run = async () => {
  // Load the AWS SDK for Node.js

  // Create IAM service object
  try {
    const data = await iam.send(
      new ListAttachedRolePoliciesCommand(paramsRoleList)
    );
    const myRolePolicies = data.AttachedPolicies;
    myRolePolicies.forEach(function (val, index, array) {
      if (myRolePolicies[index].PolicyName === "AmazonDynamoDBFullAccess") {
        const params = {
          PolicyArn: "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess",
          paramsRoleList,
        };
        try {
          const results = iam.send(new DetachRolePolicyCommand(paramsRoleList));
          console.log("Policy detached from role successfully");
          process.exit();
        } catch (err) {
          console.log("Unable to detach policy from role", err);
        }
      } else {
      }
    });
  } catch (err) {
    console.log("User " + process.argv[2] + " does not exist.");
  }
};
run();
// snippet-end:[iam.JavaScript.policies.detachRolePolicyV3]
exports.run = run; //for unit tests only
