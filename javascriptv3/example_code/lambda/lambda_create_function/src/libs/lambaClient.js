/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
lambdaClient.js is a helper function that creates the AWS Lambda service clients.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID - an Amazon Cognito Identity Pool ID.
*/
// snippet-start:[lambda.JavaScript.general-examples-lambda-create-function.lambdaClient]
const { LambdaClient } = require ("@aws-sdk/client-lambda" );
const {
  fromCognitoIdentityPool,
} = require ( "@aws-sdk/credential-provider-cognito-identity" );
const { CognitoIdentityClient }  = require ("@aws-sdk/client-cognito-identity" );

// Set the AWS Region.
const REGION = "REGION"; // e.g., 'us-east-2'
const IDENTITY_POOL_ID = "eu-west-1:dc7d706a-1f07-4fa5-baa7-edfabc05f293";

// Create an AWS Lambda client service object that initializes the Amazon Cognito credentials provider.
const lambdaClient = new LambdaClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: IDENTITY_POOL_ID
  }),
});
module.exports = {lambdaClient}

// snippet-end:[lambda.JavaScript.general-examples-lambda-create-function.lambdaClient]
