/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_updatetemplate.ts demonstrates how to update an Amazon SES email template.

Inputs (replace in code):
- REGION
- TEMPLATE_NAME
- HTML_CONTENT
- SUBJECT_LINE
- TEXT_CONTENT

Running the code:
ts-node ses_updatetemplate.ts
 */
// snippet-start:[ses.JavaScript.templates.updateTemplateV3]
// Import required AWS SDK clients and commands for Node.js
const { SESClient, UpdateTemplateCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Template: {
    TemplateName: "TEMPLATE_NAME", //TEMPLATE_NAME
    HtmlPart: "HTML_CONTENT", //HTML_CONTENT; i.e., HTML content in the email
    SubjectPart: "SUBJECT_LINE", //SUBJECT_LINE; i.e., email subject line
    TextPart: "TEXT_CONTENT", //TEXT_CONTENT; i.e., body of email
  },
};

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new UpdateTemplateCommand(params));
    console.log("Template Updated");
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.updateTemplateV3]

