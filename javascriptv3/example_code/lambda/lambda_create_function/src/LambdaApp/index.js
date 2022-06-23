/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.

Purpose:
index.js is a browser script that executes a pre-existing AWS Lambda function.

It is part of a tutorial demonstrating how create and deploy an AWS Lambda function. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.

Inputs (replace in code):
- REGION
- LAMBDA_FUNCTION
- IDENTITY_POOL_ID

*/
// snippet-start:[lambda.JavaScript.general-examples-lambda-create-function.indexV3]

// Load the required clients and packages.
const { InvokeCommand } = require ("@aws-sdk/client-lambda" );
const { lambdaClient } = require ( "../libs/lambdaClient" );

// Set the parmaeters.
const params={
  // The name of the AWS Lambda function.
  FunctionName: "LAMBDA_FUNCTION",
  InvocationType: "RequestResponse",
  LogType: "None"
}

// Call the Lambda function.
window.createTable = async () => {
  try {
    const data = await lambdaClient.send(new InvokeCommand(params));
    console.log("Table Created", data);
    document.getElementById('message').innerHTML = "Success, table created"
  } catch (err) {
    console.log("Error", err);
  }
};

// snippet-end:[lambda.JavaScript.general-examples-lambda-create-function.indexV3]
