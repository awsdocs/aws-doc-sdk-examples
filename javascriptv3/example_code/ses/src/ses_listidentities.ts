/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_listidentities.ts demonstrates how to list all the identities for an AWS account.

Inputs (replace in code):
- REGION
- IDENTITY_TYPE

Running the code:
ts-node ses_listidentities.ts
*/
// snippet-start:[ses.JavaScript.identities.listIdentitiesV3]
// Import required AWS SDK clients and commands for Node.js
const { SESClient, ListIdentitiesCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
var params = {
  IdentityType: "IDENTITY_TYPE", // IDENTITY_TYPE: "EmailAddress' or 'Domain'
  MaxItems: 10,
};

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new ListIdentitiesCommand(params));
    console.log("Success.", data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.identities.listIdentitiesV3]

