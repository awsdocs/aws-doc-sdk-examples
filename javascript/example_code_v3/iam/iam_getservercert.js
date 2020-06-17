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
iam_getservercert.test.js demonstrates how to retrieve information about an IAM SSL/TLS server certificate.

Inputs (in command line below):
- REGION
- CERTIFICATE_NAME

Running the code:
node iam_getservercert.js REGION CERTIFICATE_NAME
 */
// snippet-start:[iam.JavaScript.certs.getServerCertificate]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, GetServerCertificateCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  const params = {
    ServerCertificateName: process.argv[3]
  };
  try {
    const data = await iam.send(new GetServerCertificateCommand(params));
    console.log("Success", data);
  }
  catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[iam.JavaScript.certs.getServerCertificate]
exports.run = run;
