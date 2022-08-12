/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_deletetemplate.js demonstrates how to delete an Amazon SES email template.

Inputs (replace in code):
- TEMPLATE_NAME

Running the code:
node ses_deletetemplate.st
*/
// snippet-start:[ses.JavaScript.templates.deleteTemplateV3]
// Import required AWS SDK clients and commands for Node.js
import { DeleteTemplateCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";
// Set the parameters
const params = { TemplateName: "TEMPLATE_NAME" };

const run = async () => {
  try {
    const data = await sesClient.send(new DeleteTemplateCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.deleteTemplateV3]
// For unit tests only.
export { run, params }
