/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020.

Purpose:
secrets_getsecretvalue.ts demonstrates how to retrieve a secret from Amazon Secrets Manager.

Inputs (replace in code):
- SECRET_ID

Running the code:
node secrets_getsecretvalue.js
 */

// snippet-start:[secrets.JavaScript.retrieve.getSecretsValueV3]
// Import required AWS SDK clients and commands for Node.js
import {
  GetSecretValueCommand,
} from "@aws-sdk/client-secrets-manager";
import { secretsClient } from "./libs/secretsClient.js" ;

// Set the parameters
const params = {
  SecretId: "SECRET_ID", //e.g. arn:aws:secretsmanager:REGION:XXXXXXXXXXXX:secret:mysecret-XXXXXX
};

const run = async () => {
  let data;
  try {
    data = await secretsClient.send(new GetSecretValueCommand(params));
    console.log("data", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("err", err);
  }
  let secret;
  if ("SecretString" in data) {
    secret = data.SecretString;
  } else {
    console.log("else:", data);

    // Create a buffer
    const buff = new Buffer(data.SecretBinary, "base64");
    secret = buff.toString("ascii");
  }
  return secret;
};
run();
// snippet-end:[secrets.JavaScript.retrieve.getSecretsValueV3]
// For unit tests.
//module.exports = {run, params}
