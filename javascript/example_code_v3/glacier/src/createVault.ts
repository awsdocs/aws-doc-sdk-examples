/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/glacier-example-creating-a-vault.html.

Purpose:
createVault.ts demonstrates how to create a vault in Amazon S3 Glacier.

Inputs (into code):
- REGION
- VAULT_NAME

Running the code:
ts-node createVault.ts
 */

// snippet-start:[glacier.JavaScript.vault.createVaultV3]
// Load the SDK for JavaScript
const { Glacier, CreateVaultCommand } = require("@aws-sdk/client-glacier");

// Set the AWS Region
const REGION = "REGION"; // e.g. 'us-east-1'

// Set the parameters
const vaultname = "VAULT_NAME"; // VAULT_NAME
const params = { vaultName: vaultname };

// Instantiate an S3 Glacier client
const glacier = new Glacier(REGION);

const run = async () => {
  try {
    const data = await glacier.send(new CreateVaultCommand(params));
    console.log("Success, vault created!");
  } catch (err) {
    console.log("Error");
  }
};
run();
// snippet-end:[glacier.JavaScript.vault.createVaultV3]
//for unit tests only
module.exports = {run};
