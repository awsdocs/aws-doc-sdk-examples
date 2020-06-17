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
ses_gettemplate.js demonstrates how to retrieve an Amazon SES email template.

Inputs:
- REGION (in commmand line input below)
- TEMPLATE_NAME (in commmand line input below)

Running the code:
node ses_gettemplate.js REGION TEMPLATE_NAME
 */
// snippet-start:[ses.JavaScript.templates.getTemplate]
async function run() {
    try {
        const {SES, GetTemplateCommand} = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        const ses = new SES(region);
        const params = {TemplateName: process.argv[3]};
        // Create deleteReceiptRule params
        const data = await ses.send(new GetTemplateCommand(params));
        console.log('Success', data)
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.templates.getTemplate]
exports.run = run;
