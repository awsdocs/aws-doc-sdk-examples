/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-function-prep.html.
Purpose:
lambda-function-setup.ts demonstrates how to create an AWS Lambda function.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- ZIP_FILE_NAME
- IAM_ROLE_ARN

Running the code:
ts-node lambda-function-setup.ts
*/

// snippet-start:[lambda.JavaScript.tutorial.LambdaFunctionSetUpV3]
// Load the Lambda client
const {
  LambdaClient,
  CreateFunctionCommand
} = require("@aws-sdk/client-lambda");

//Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate a Lambda client
const lambda = new LambdaClient({ region: REGION });

//Set the parameters
const params = {
  Code: {
    S3Bucket: "BUCKET_NAME", // BUCKET_NAME
    S3Key: "ZIP_FILE_NAME", // ZIP_FILE_NAME
  },
  FunctionName: "slotpull",
  Handler: "index.handler",
  Role: "IAM_ROLE_ARN", // IAM_ROLE_ARN; e.g., arn:aws:iam::650138640062:role/v3-lambda-tutorial-lambda-role
  Runtime: "nodejs12.x",
  Description: "Slot machine game results generator",
};

const run = async () => {
  try {
    const data = await lambda.send(new CreateFunctionCommand(params));
    console.log("Success", data); // successful response
  } catch (err) {
    console.log("Error", err); // an error occurred
  }
};
run();
// snippet-end:[lambda.JavaScript.tutorial.LambdaFunctionSetUpV3]

