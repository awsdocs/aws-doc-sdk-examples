/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html.

Purpose:
lambda-function-setup.ts demonstrates how to create an AWS Lambda function.
It is part of a tutorial demonstrates how to create an API that triggers a AWS Lambda function that scans an
Amazon DynamoDB table of employees' information and send an Amazon Simple Notification Service (Amazon SNS)
message based on the results. It demonstrates how toTo run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- ZIP_FILE_NAME
- FUNCTION_NAME
- IAM_ROLE_ARN

Running the code:
ts-node lambda-function-setup.ts
*/

// snippet-start:[lambda.JavaScript.general-examples-dynamodb-lambda.LambdaFunctionSetUpV3]
// Load the required AWS Lambda client and commands.
const {
  LambdaClient,
  CreateFunctionCommand,
} = require("@aws-sdk/client-lambda");

// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate an AWS Lambda client service object.
const lambda = new LambdaClient({ region: REGION });

// Set the parameters.
const params = {
  Code: {
    S3Bucket: "BUCKET_NAME", // BUCKET_NAME
    S3Key: "ZIP_FILE_NAME", // ZIP_FILE_NAME
  },
  FunctionName: "LAMBDA_FUNCTION_NAME",
  Handler: "index.handler",
  Role: "IAM_ROLE_ARN", // IAM_ROLE_ARN; e.g., arn:aws:iam::650138640062:role/v3-lambda-tutorial-lambda-role
  Runtime: "nodejs12.x",
  Description:
    "Scans a DynamoDB table of employee details and using Amazon Simple Notification Services (SNS) to " +
    "send employees an email the each anniversary of their start-date.",
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
// snippet-end:[lambda.JavaScript.general-examples-dynamodb-lambda.LambdaFunctionSetUpV3]
