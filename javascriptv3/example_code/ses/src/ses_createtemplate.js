/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_createtemplate.js demonstrates how to create an Amazon SES email template.

Running the code:
node ses_createtemplate.js
*/

// snippet-start:[ses.JavaScript.templates.createTemplateV3]

import { CreateTemplateCommand } from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";
import { getUniqueName } from "../../libs/index.js";

const TEMPLATE_NAME = getUniqueName("TestTemplateName");

const createCreateTemplateCommand = () => {
  return new CreateTemplateCommand({
    Template: {
      TemplateName: TEMPLATE_NAME,
      HtmlPart: "HTML_CONTENT",
      SubjectPart: "SUBJECT",
      TextPart: "TEXT_CONTENT",
    },
  });
};

const run = async () => {
  const createTemplateCommand = createCreateTemplateCommand();

  try {
    return await sesClient.send(createTemplateCommand);
  } catch (err) {
    console.log("Failed to create template.", err);
    return err;
  }
};
// snippet-end:[ses.JavaScript.templates.createTemplateV3]

export { run, TEMPLATE_NAME };
