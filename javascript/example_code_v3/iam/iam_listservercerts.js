/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-server-certificates.html.

Purpose:
iam_listservercerts.js demonstrates how to list the IAM SSL/TLS server certificates.

Inputs (into command line below):
- REGION

Running the code:
node iam_listservercerts.js REGION
 */
// snippet-start:[iam.JavaScript.v3.certs.listServerCertificates]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, ListServerCertificatesCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);

async function run() {
  try {
    const data = await iam.send(new ListServerCertificatesCommand({}));
    console.log("Success", data);
  }
  catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.v3.certs.listServerCertificates]
exports.run = run; //for unit tests only
