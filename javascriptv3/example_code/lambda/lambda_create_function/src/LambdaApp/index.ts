/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.

Purpose:
index.js is a browser script that executes a pre-existing Amazon Lambda function.
It is part of a tutorial demonstrating how to build and deploy an app that transcribes and displays
voice recordings for authenticated users. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.


Inputs (replace in code):
- REGION
- LAMBDA_FUNCTION
- IDENTITY_POOL_ID

Running the code:
ts-node ddb-table-create.ts
*/
// snippet-start:[lambda.JavaScript.general-examples.indexV3]

// Load the required clients and packages.
const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");
const { LambdaClient, InvokeCommand } = require("@aws-sdk/client-lambda");

// Set the AWS Region.
const REGION = "REGION"; // e.g., 'us-east-2'

// Set the parmaeters.
const params={
  // The name of the Amazon Lambda function.
  FunctionName: "LAMBDA_FUNCTION",
  InvocationType: "RequestResponse",
  LogType: "None"
}

// Create an Amazon Lambda client service object that initializes the Amazon Cognito credentials provider.
const lambda = new LambdaClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID e.g., eu-west-1:xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx
  }),
});

// Call the Lambda function.
window.createTable = async () => {
  try {
    const data = await lambda.send(new InvokeCommand(params));
    console.log("Table Created", data);
    document.getElementById('message').innerHTML = "Success, table created"
  } catch (err) {
    console.log("Error", err);
  }
};

// snippet-end:[lambda.JavaScript.general-examples.indexV3]
