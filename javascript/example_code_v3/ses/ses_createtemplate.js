/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-creating-template.html

Purpose:
ses_createtemplate.js demonstrates how to create an Amazon SES email template.

Inputs:
- REGION (in commmand line input below)
- TEMPLATE_NAME (in commmand line input below)
- HTML_CONTENT (in code): HTML tagged content of email
- SUBJECT (in code): The subject of the email
- TEXT_CONTENT (in code): Text content of the email

Running the code:
node ses_createtemplate.js REGION TEMPLATE_NAME
*/

// snippet-start:[ses.JavaScript.templates.createTemplate]
async function run() {
    try {
        const {SES, CreateTemplateCommand} = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        // Create the SES service object
        const ses = new SES(region);
        // Create createTemplate params
        const params = {
            Template: {
                TemplateName: process.argv[3], /* required */
                HtmlPart: 'HTML_CONTENT',
                SubjectPart: 'SUBJECT',
                TextPart: 'TEXT_CONTENT'
            }
        };
        const data = await ses.send(new CreateTemplateCommand(params));
        console.log(data)
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.templates.createTemplate]
exports.run = run;
