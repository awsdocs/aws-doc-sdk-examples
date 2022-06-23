/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_createpolicy.js demonstrates how to create a managed policy for an AWS account.

Inputs :
- RESOURCE_ARN
- DYNAMODB_POLICY_NAME
- IAM_POLICY_NAME

Running the code:
node iam_createpolicy.js
 */
// snippet-start:[iam.JavaScript.policies.createPolicyV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { CreatePolicyCommand } from "@aws-sdk/client-iam";

// Set the parameters.
const myManagedPolicy = {
  Version: "2012-10-17",
  Statement: [
    {
      Effect: "Allow",
      Action: "logs:CreateLogGroup",
      Resource: "RESOURCE_ARN", // RESOURCE_ARN
    },
    {
      Effect: "Allow",
      Action: [
        "dynamodb:DeleteItem",
        "dynamodb:GetItem",
        "dynamodb:PutItem",
        "dynamodb:Scan",
        "dynamodb:UpdateItem",
      ],
      Resource: "DYNAMODB_POLICY_NAME", // DYNAMODB_POLICY_NAME; For example, "myDynamoDBName".
    },
  ],
};
export const params = {
  PolicyDocument: JSON.stringify(myManagedPolicy),
  PolicyName: "IAM_POLICY_NAME",
};

export const run = async () => {
  try {
    const data = await iamClient.send(new CreatePolicyCommand(params));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.policies.createPolicyV3]

