// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cross-service.lambda-from-browser.JavaScript.ddbclient]
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";

const IDENTITY_POOL_ID = "IDENTITY_POOL_ID"; // An Amazon Cognito Identity Pool ID.

// Create an Amazon DynamoDB client service object that initializes the Amazon Cognito credentials provider.
const ddbClient = new DynamoDBClient({
  credentials: fromCognitoIdentityPool({
    // The empty parameters in the CognitoIdentityClient constructor assume
    // a region is configured in your shared configuration file.
    client: new CognitoIdentityClient({}),
    identityPoolId: IDENTITY_POOL_ID,
  }),
});
export { ddbClient };

// snippet-end:[cross-service.lambda-from-browser.JavaScript.ddbclient]
