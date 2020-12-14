/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-server-certificates.html.

Purpose:
iam_deleteservercert.ts demonstrates how to delete an IAM SSL/TLS server certificate.

Inputs :
- REGION
- CERTIFICATE_NAME

Running the code:
ts-node iam_deleteservercert.ts
 */
// snippet-start:[iam.JavaScript.certs.deleteServerCertificateV3]
// Import required AWS SDK clients and commands for Node.js
const {
  IAMClient,
  DeleteServerCertificateCommand,
} = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { ServerCertificateName: "CERTIFICATE_NAME" }; // CERTIFICATE_NAME

// Create IAM service object
const iam = new IAMClient({ region: REGION });

const run = async () => {
  try {
    const data = await iam.send(new DeleteServerCertificateCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.certs.deleteServerCertificateV3]

