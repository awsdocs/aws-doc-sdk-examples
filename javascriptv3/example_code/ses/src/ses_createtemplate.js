/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_createtemplate.js demonstrates how to create an Amazon SES email template.

Inputs (replace in code):
- TEMPLATE_NAME
- HTML_CONTENT (into code; HTML tagged content of email)
- SUBJECT (into code; the subject of the email)
- TEXT_CONTENT (into code; text content of the email)

Running the code:
node ses_createtemplate.js
*/

// snippet-start:[ses.JavaScript.templates.createTemplateV3]

// Import required AWS SDK clients and commands for Node.js
import { CreateTemplateCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";
// Create createTemplate params
const params = {
  Template: {
    TemplateName: "TEMPLATE_NAME", //TEMPLATE_NAME
    HtmlPart: "HTML_CONTENT",
    SubjectPart: "SUBJECT",
    TextPart: "TEXT_CONTENT",
  },
};

const run = async () => {
  try {
    const data = await sesClient.send(new CreateTemplateCommand(params));
    console.log(
      "Success",
      data
    );
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.createTemplateV3]
// For unit tests only.
/// module.exports ={run, params};
