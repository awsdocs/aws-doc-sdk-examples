/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
dynamoClient.js is a helper function that creates the Amazon DynamoDB service clients.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID - an Amazon Cognito Identity Pool ID.
*/
// snippet-start:[lambda.JavaScript.general-examples-lambda-create-function.dynamoClient]
const { CognitoIdentityClient } = require ( "@aws-sdk/client-cognito-identity" );
const { fromCognitoIdentityPool } = require ( "@aws-sdk/credential-provider-cognito-identity" );
const { DynamoDBClient } = require ( "@aws-sdk/client-dynamodb" );

const REGION = "REGION";
const IDENTITY_POOL_ID = "IDENTITY_POOL_ID"; // An Amazon Cognito Identity Pool ID.

// Create an Amazon DynamoDB service client object.
const dynamoClient = new DynamoDBClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: IDENTITY_POOL_ID,
  }),
});

module.exports = { dynamoClient };
// snippet-end:[lambda.JavaScript.general-examples-lambda-create-function.dynamoClient]
