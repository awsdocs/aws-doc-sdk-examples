/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-key-pairs.html

Purpose:
ec2_deletekeypair.ts demonstrates how to delete a key pair from an Amazon EC2 instance.

Inputs (replace in code):
- REGION
- KEY_PAIR_NAME

Running the code:
ts-node ec2_deletekeypair.ts
 */
// snippet-start:[ec2.JavaScript.keypairs.deleteKeyPairV3]
// Import required AWS SDK clients and commands for Node.js
const { EC2Client, DeleteKeyPairCommand } = require("@aws-sdk/client-ec2");
// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { KeyName: "KEY_PAIR_NAME" }; //KEY_PAIR_NAME

// Create EC2 service object
const ec2client = new EC2Client(REGION);

const run = async () => {
  try {
    const data = await ec2client.send(new DeleteKeyPairCommand(params));
    console.log("Key Pair Deleted");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.keypairs.deleteKeyPairV3]

