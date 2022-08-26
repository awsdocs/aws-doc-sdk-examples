/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_listidentities.js demonstrates how to list all the identities for an AWS account.

Running the code:
node ses_listidentities.js
*/
// snippet-start:[ses.JavaScript.identities.listIdentitiesV3]
import { ListIdentitiesCommand } from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

const createListIdentitiesCommand = () =>
  new ListIdentitiesCommand({ IdentityType: "EmailAddress", MaxItems: 10 });

const run = async () => {
  const listIdentitiesCommmand = createListIdentitiesCommand();

  try {
    return await sesClient.send(listIdentitiesCommmand);
  } catch (err) {
    console.log("Failed to list identities.", err);
    return err;
  }
};
// snippet-end:[ses.JavaScript.identities.listIdentitiesV3]

export { run };
