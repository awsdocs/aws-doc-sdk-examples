/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
s3Client.js is a helper function that creates an Amazon Simple Storage Service (Amazon S3) service client using ES6 syntax.

Inputs (replace in code):
- REGION
*/
// snippet-start:[s3.JavaScript.detect-ppe.createclientv3]

// Create service client module using CommonJS syntax.
import { S3Client } from "@aws-sdk/client-s3";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";

// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
const IDENTITY_POOL_ID = "IDENTITY_POOL_ID"; //e.g. "us-east-1"

// Create an Amazon S3 service client object.
const s3Client = new S3Client({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: IDENTITY_POOL_ID,
  }),
});
export { s3Client };
// snippet-end:[s3.JavaScript.detect-ppe.createclientv3]
