/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_deletetemplate.ts demonstrates how to delete an Amazon SES email template.

Inputs (replace in code):
- REGION
- TEMPLATE_NAME

Running the code:
ts-node ses_deletetemplate.st
*/
// snippet-start:[ses.JavaScript.templates.deleteTemplateV3]
// Import required AWS SDK clients and commands for Node.js
const { SESClient, DeleteTemplateCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { TemplateName: "TEMPLATE_NAME" };

// Create SES service object
const ses = new SESClient({ region: REGION });

const run = async () => {
  try {
    const data = await ses.send(new DeleteTemplateCommand(params));
    console.log("Template deleted.");
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.deleteTemplateV3]

