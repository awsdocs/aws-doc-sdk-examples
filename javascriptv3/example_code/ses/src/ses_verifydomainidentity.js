/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_verifydomainidentity.js demonstrates how to add a domain to the list of Amazon SES identities and attempts to verify it.

Inputs (replace in code):
- DOMAIN_NAME

Running the code:
node ses_verifydomainidentity.js
 */
// snippet-start:[ses.JavaScript.identities.verifyDomainIdentityV3]
// Import required AWS SDK clients and commands for Node.js
import {
  VerifyDomainIdentityCommand,
}  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

// Set the parameters
const params = { Domain: "DOMAIN_NAME" }; //DOMAIN_NAME

const run = async () => {
  try {
    const data = await sesClient.send(new VerifyDomainIdentityCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.identities.verifyDomainIdentityV3]
// For unit tests only.
export { run, params }
