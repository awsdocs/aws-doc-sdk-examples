/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_verifyemailidentity.js demonstrates how to send an Amazon SES verification email.

Inputs (replace in code):
- ADDRESS@DOMAIN.EXT

Running the code:
node ses_verifyemailidentity.js

 */
// snippet-start:[ses.JavaScript.identities.verifyEmailIdentityV3]
// Import required AWS SDK clients and commands for Node.js
import {
    VerifyEmailIdentityCommand
}  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

// Set the parameters
const params = { EmailAddress: "ADDRESS@DOMAIN.EXT" }; //ADDRESS@DOMAIN.EXT; e.g., name@example.com


const run = async () => {
  try {
    const data = await sesClient.send(new VerifyEmailIdentityCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.identities.verifyEmailIdentityV3]
// For unit tests only.
// module.exports ={run, params};
