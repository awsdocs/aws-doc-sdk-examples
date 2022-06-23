/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/kinesis-examples-capturing-page-scrolling.html.

Purpose:
kinesisClient.js is a helper function that creates an Amazon Kinesis service client.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID

*/
// snippet-start:[kinesis.JavaScript.createclientv3]
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { KinesisClient } from "@aws-sdk/client-kinesis";

// Set the AWS Region.
const REGION = "REGION";

// Set the Amazon Kinesis Service object.
const kinesisClient = new KinesisClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID
  }),
});
export { kinesisClient };
// snippet-end:[kinesis.JavaScript.createclientv3]
