/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_createtemplate.ts demonstrates how to create an AWS SES email template.

Inputs (replace in code):
- REGION
- TEMPLATE_NAME
- HTML_CONTENT (into code; HTML tagged content of email)
- SUBJECT (into code; the subject of the email)
- TEXT_CONTENT (into code; text content of the email)

Running the code:
ts-node ses_createtemplate.ts
*/

// snippet-start:[ses.JavaScript.templates.createTemplateV3]

// Import required AWS SDK clients and commands for Node.js
const { SESClient, CreateTemplateCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create createTemplate params
const params = {
  Template: {
    TemplateName: "TEMPLATE_NAME", //TEMPLATE_NAME
    HtmlPart: "HTML_CONTENT",
    SubjectPart: "SUBJECT",
    TextPart: "TEXT_CONTENT",
  },
};

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new CreateTemplateCommand(params));
    console.log(
      "Success, template created; requestID",
      data.$metadata.requestId
    );
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.createTemplateV3]

