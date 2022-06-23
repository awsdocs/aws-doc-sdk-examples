/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-server-certificates.html.

Purpose:
iam_getservercert.js demonstrates how to retrieve information about an IAM SSL/TLS server certificate.

Inputs :
- CERTIFICATE_NAME

Running the code:
node iam_getservercert.js
 */
// snippet-start:[iam.JavaScript.certs.getServerCertificateV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { GetServerCertificateCommand } from "@aws-sdk/client-iam";

// Set the parameters.
export const params = { ServerCertificateName: "CERTIFICATE_NAME" }; //CERTIFICATE_NAME

export const run = async () => {
  try {
    const data = await iamClient.send(new GetServerCertificateCommand(params));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[iam.JavaScript.certs.getServerCertificateV3]

