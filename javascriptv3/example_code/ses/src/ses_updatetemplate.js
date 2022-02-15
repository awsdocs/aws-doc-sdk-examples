/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_updatetemplate.js demonstrates how to update an Amazon SES email template.

Inputs (replace in code):
- TEMPLATE_NAME
- HTML_CONTENT
- SUBJECT_LINE
- TEXT_CONTENT

Running the code:
node ses_updatetemplate.js
 */
// snippet-start:[ses.JavaScript.templates.updateTemplateV3]
// Import required AWS SDK clients and commands for Node.js
import { UpdateTemplateCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

// Set the parameters
const params = {
  Template: {
    TemplateName: "TEMPLATE_NAME", //TEMPLATE_NAME
    HtmlPart: "HTML_CONTENT", //HTML_CONTENT; i.e., HTML content in the email
    SubjectPart: "SUBJECT_LINE", //SUBJECT_LINE; i.e., email subject line
    TextPart: "TEXT_CONTENT", //TEXT_CONTENT; i.e., body of email
  },
};

const run = async () => {
  try {
    const data = await sesClient.send(new UpdateTemplateCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.updateTemplateV3]
// For unit tests only.
// module.exports ={run, params};
