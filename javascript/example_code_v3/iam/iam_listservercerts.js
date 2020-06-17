/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/iam-examples-server-certificates.html

Purpose:
iam_listservercerts.js demonstrates how to list the IAM SSL/TLS server certificates.

Inputs (in command line below):
- REGION

Running the code:
node iam_listservercerts.js REGION
 */
// snippet-start:[iam.JavaScript.certs.listServerCertificates]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, ListServerCertificatesCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  try {
    const data = await iam.send(new ListServerCertificatesCommand({}));
    console.log("Success", data);
  }
  catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[iam.JavaScript.certs.listServerCertificates]
exports.run = run;
