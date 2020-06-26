/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) top
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_updatetemplate.test.js demonstrates how to update an Amazon SES email template.

Inputs:
- REGION (into command line below)
- TEMPLATE_NAME (into command line below)
- HTML_CONTENT (replace in code; HTML content in the email.)
- SUBJECT_LINE (replace in code; email subject line.)
- TEXT_CONTENT (replace in code; body of email.)

Running the code:
node ses_updatetemplate.js REGION TEMPLATE_NAME
 */
// snippet-start:[ses.JavaScript.v3.templates.updateTemplate]
// Import required AWS SDK clients and commands for Node.js
const {SES, UpdateTemplateCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {
  Template: {
    TemplateName: process.argv[3], /* required */
    HtmlPart: 'HTML_CONTENT',
    SubjectPart: 'SUBJECT_LINE',
    TextPart: 'TEXT_CONTENT'
  }
};

async function run() {
  try {
    const data = await ses.send(new UpdateTemplateCommand(params));
    console.log("Template Updated");
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.v3.templates.updateTemplate]
exports.run = run; //for unit tests only
