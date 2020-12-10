/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_listaccesskeys.ts demonstrates how to retrieve information about the access keys of an IAM user.

Inputs :
- REGION
- IAM_USER_NAME

Running the code:
ts-node iam_listaccesskeys.ts
 */

// snippet-start:[iam.JavaScript.keys.listAccessKeysV3]
// Import required AWS SDK clients and commands for Node.js
const { IAMClient, ListAccessKeysCommand } = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  MaxItems: 5,
  UserName: "IAM_USER_NAME", //IAM_USER_NAME
};

// Create IAM service object
const iam = new IAMClient({ region: REGION });

const run = async () => {
  try {
    const data = await iam.send(new ListAccessKeysCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.keys.listAccessKeysV3]

