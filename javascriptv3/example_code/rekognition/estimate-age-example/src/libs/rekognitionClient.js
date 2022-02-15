/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/rekognition-estimate-age-example.html.

Purpose:
rekognitionClient.js is a helper function that creates an Amazon Rekognition service client.

Inputs (replace in code):
- REGION

*/
// snippet-start:[rekognition.JavaScript.createclientv3]
import { RekognitionClient } from "@aws-sdk/client-rekognition";
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"

// Create an Amazon Transcribe service client object.
const rekognitionClient = new RekognitionClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: "IDENTITY_POOL_ID",
  }),
});

export { rekognitionClient };
// snippet-end:[rekognition.JavaScript.createclientv3]
