/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-iam-role-setup.html.

Purpose:
lambda-role-setup.ts demonstrates how create an AWS IAM role.

Inputs (replace in code):
- REGION
- NEW_ROLENAME

Running the code:
ts-node lambda-role-setup.ts
*/
// snippet-start:[lambda.JavaScript.tutorial.LambdaRoleSetUpV3]
// Import a non-modular IAM client
const {
  IAMClient,
  CreateRoleCommand,
  AttachRolePolicyCommand
} = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate the IAM client
const iam = new IAMClient({ region: REGION });

// Set the parameters
const ROLE = "NEW_ROLENAME"; //NEW_ROLENAME
const myPolicy = {
  Version: "2012-10-17",
  Statement: [
    {
      Effect: "Allow",
      Principal: {
        Service: "lambda.amazonaws.com",
      },
      Action: "sts:AssumeRole",
    },
  ],
};

const createParams = {
  AssumeRolePolicyDocument: JSON.stringify(myPolicy),
  RoleName: ROLE,
};

const lambdaPolicyParams = {
  PolicyArn: "arn:aws:iam::aws:policy/service-role/AWSLambdaRole",
  RoleName: ROLE,
};

const dynamoPolicyParams = {
  PolicyArn: "arn:aws:iam::aws:policy/AmazonDynamoDBReadOnlyAccess",
  RoleName: ROLE,
};

const run = async () => {
  try {
    const data = await iam.send(new CreateRoleCommand(createParams));
    console.log("Role ARN is", data.Role.Arn); // successful response
  } catch (err) {
    console.log("Error when creating role."); // an error occurred
    throw err;
  }
  try {
    await iam.send(new AttachRolePolicyCommand(lambdaPolicyParams));
    console.log("AWSLambdaRole policy attached"); // successful response
  } catch (err) {
    console.log("Error when attaching Lambda policy to role."); // an error occurred
    throw err;
  }
  try {
    await iam.send(new AttachRolePolicyCommand(dynamoPolicyParams));
    console.log("DynamoDB read-only policy attached"); // successful response
  } catch (err) {
    console.log("Error when attaching dynamodb policy to role."); // an error occurred
    throw err;
  }
};

run();
// snippet-end:[lambda.JavaScript.tutorial.LambdaRoleSetUpV3]

