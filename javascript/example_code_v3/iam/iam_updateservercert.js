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
iam_updateservercert.js demonstrates how to update the name of an IAM SSL/TLS server certificate.

Inputs (in command line below):
- REGION
- CERTIFICATE_NAME
- NEW_CERTIFICATE_NAME

Running the code:
node iam_updateservercert.js REGION CERTIFICATE_NAME NEW_CERTIFICATE_NAME
 */
// snippet-start:[iam.JavaScript.certs.updateServerCertificate]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, UpdateServerCertificateCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  var params = {
    ServerCertificateName: process.argv[3],
    NewServerCertificateName: process.argv[4]
  };
  try {
    const data = await iam.send(new UpdateServerCertificateCommand(params));
    console.log("Success", data);
  } catch(err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.certs.updateServerCertificate]
exports.run = run;
