/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_getpolicy.js demonstrates how to retrieve information about an IAM managed policy.

Inputs (into command line below):
- REGION

Running the code:
node iam_getpolicy.js REGION
 */
// snippet-start:[iam.JavaScript.v3.policies.getPolicy]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, GetPolicyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {
  PolicyArn: 'arn:aws:iam::aws:policy/AWSLambdaExecute'
};

async function run() {
  try {
    const data = await iam.send(new GetPolicyCommand(params));
    console.log("Success", data.Policy.Description);
  }
  catch (err) {
  console.log("Error", err);
}
};
run();
// snippet-end:[iam.JavaScript.v3.policies.getPolicy]
exports.run = run; //for unit tests only
