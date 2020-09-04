/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
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
const { SES, DeleteTemplateCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = { TemplateName: "TEMPLATE_NAME" };

// Create SES service object
const ses = new SES(REGION);

const run = async () => {
  try {
    const data = await ses.send(new DeleteTemplateCommand(params));
    console.log("Template Deleted");
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.deleteTemplateV3]
module.exports = {run}; //for unit tests only
