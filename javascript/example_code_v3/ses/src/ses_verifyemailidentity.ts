/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_verifyemailidentity.ts demonstrates how to send an Amazon SES verification email.

Inputs (replace in code):
- REGION
- ADDRESS@DOMAIN.EXT

Running the code:
ts-node ses_verifyemailidentity.ts

 */
// snippet-start:[ses.JavaScript.identities.verifyEmailIdentityV3]

// Import required AWS SDK clients and commands for Node.js
const { SES, VerifyEmailIdentityCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = { EmailAddress: "ADDRESS@DOMAIN.EXT" }; //ADDRESS@DOMAIN.EXT; e.g., name@example.com

// Create SES service object
const ses = new SES(REGION);

const run = async () => {
  try {
    const data = await ses.send(new VerifyEmailIdentityCommand(params));
    console.log("Email verification initiated");
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.identities.verifyEmailIdentityV3]
// module.exports = {run}; //for unit tests only
