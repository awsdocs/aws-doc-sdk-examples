/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/client-secrets-manager.html.

Purpose:
secretsClient.js is a helper function that creates an Amazon Secrets Manager service client.

Inputs (replace in code):
- REGION
*/
// snippet-start:[secrets.JavaScript.createclientv3]
import { SecretsManagerClient } from "@aws-sdk/client-secrets-manager";
// Set the AWS Region.
const REGION = "REGION";
//Set the Secrets Manager Service Object
const secretsClient = new SecretsManagerClient({ region: REGION });
export { secretsClient };
// snippet-end:[secrets.JavaScript.createclientv3]
