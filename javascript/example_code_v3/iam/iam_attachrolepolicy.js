/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions
and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/iam-examples-policies.html

Purpose:
iam_attachrolepolicy.test.js demonstrates how to attach a managed policy to an IAM role.

Inputs (in command line below):
- REGION
- ROLE_NAME

Running the code:
node iam_attachrolepolicy.js REGION ROLE_NAME

 */

// snippet-start:[iam.JavaScript.policies.attachRolePolicy]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, ListAttachedRolePoliciesCommand, AttachRolePolicyCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  try {
    const paramsRoleList = {
      RoleName: process.argv[3]
    };
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
// snippet-end:[iam.JavaScript.policies.attachRolePolicy]
exports.run = run;
