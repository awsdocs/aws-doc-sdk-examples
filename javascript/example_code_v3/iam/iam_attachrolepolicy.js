/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_attachrolepolicy.js demonstrates how to attach a managed policy to an IAM role.

Inputs :
- REGION
- ROLE_NAME

Running the code:
node iam_attachrolepolicy.js

 */

// snippet-start:[iam.JavaScript.policies.attachRolePolicyV3]

// Import required AWS SDK clients and commands for Node.js
const {
  IAMClient,
  ListAttachedRolePoliciesCommand,
  AttachRolePolicyCommand,
} = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const ROLENAME = "ROLE_NAME"
const paramsRoleList = { RoleName: ROLENAME }; //ROLE_NAME

// Create IAM service object
const iam = new IAMClient(REGION);

const run = async () => {
  const iam = new IAMClient(REGION);
  try {
    const data = await iam.send(
      new ListAttachedRolePoliciesCommand(paramsRoleList)
    );
    const myRolePolicies = data.AttachedPolicies;
    myRolePolicies.forEach(function (val, index, array) {
      if (myRolePolicies[index].PolicyName === "AmazonDynamoDBFullAccess") {
        console.log(
          "AmazonDynamoDBFullAccess is already attached to this role."
        );
        process.exit();
      }
    });
    try {
      var params = {
        PolicyArn: "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess",
        RoleName: ROLENAME
      };
      const data = await iam.send(new AttachRolePolicyCommand(params));
      console.log("Role attached successfully");
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.policies.attachRolePolicyV3]
exports.run = run; //for unit tests only
