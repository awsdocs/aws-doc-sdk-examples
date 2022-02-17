/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Purpose:
lambda-function-setup.ts demonstrates how to create an AWS Lambda function.
It is part of a tutorial that demonstrates how to execute Lambda functions using Amazon CloudWatch scheduled events. To see the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- ZIP_FILE_NAME
- FUNCTION_NAME
- IAM_ROLE_ARN

Running the code:
ts-node lambda-function-setup.ts
*/

// snippet-start:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.LambdaFunctionSetUpV3]

const {
   CreateFunctionCommand,
} = require("@aws-sdk/client-lambda");
const {lambdaClient} = require ( "../libs/lambdaClient.js" );

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
      "Scans a DynamoDB table of employee details and using Amazon Simple Notification Service (Amazon SNS) to " +
      "send employees an email on the anniversary of their start-date.",
};

const run = async () => {
  try {
    const data = await lambdaClient.send(new CreateFunctionCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.LambdaFunctionSetUpV3]
