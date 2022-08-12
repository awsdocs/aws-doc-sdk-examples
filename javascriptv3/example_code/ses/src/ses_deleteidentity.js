/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_deleteidentity.js demonstrates how to delete an Amazon SES identity.

Inputs (replace in code):
- IDENTITY_TYPE
- IDENTITY_NAME

Running the code:
node ses_deleteidentity.js
*/
// snippet-start:[ses.JavaScript.identities.deleteIdentityV3]
// Import required AWS SDK clients and commands for Node.js
import { DeleteIdentityCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";
// Set the parameters
const params = {
  IdentityType: "IDENTITY_TYPE", // IDENTITY_TYPE - i.e., 'EmailAddress' or 'Domain'
  Identity: "IDENTITY_NAME",
}; // IDENTITY_NAME

const run = async () => {
  try {
    const data = await sesClient.send(new DeleteIdentityCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.identities.deleteIdentityV3]
// For unit tests only.
export { run, params }
