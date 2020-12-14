/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-key-pairs.html

Purpose:
ec2_createkeypair.ts demonstrates how to create an RSA key pair for an Amazon EC2 instance.

Inputs (replace in code):
- REGION
- MY_KEY_PAIR

Running the code:
node ec2_createkeypair.ts
 */
// snippet-start:[ec2.JavaScript.keypairs.createKeyPairV3]
// Import required AWS SDK clients and commands for Node.js
const { EC2Client, CreateKeyPairCommand } = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { KeyName: "MY_KEY_PAIR" }; //MY_KEY_PAIR

// Create EC2 service object
const ec2client = new EC2Client({ region: REGION });

const run = async () => {
  try {
    const data = await ec2client.send(new CreateKeyPairCommand(params));
    console.log(JSON.stringify(data));
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.keypairs.createKeyPairV3]

