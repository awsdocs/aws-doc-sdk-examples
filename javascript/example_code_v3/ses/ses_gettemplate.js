/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_gettemplate.js demonstrates how to retrieve an Amazon SES email template.

Inputs:
- REGION (into command line below)
- TEMPLATE_NAME (into command line below)

Running the code:
node ses_gettemplate.js REGION TEMPLATE_NAME
 */
// snippet-start:[ses.JavaScript.v3.templates.getTemplate]
// Import required AWS SDK clients and commands for Node.js
const {SES, GetTemplateCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {TemplateName: process.argv[3]};

async function run() {
    try {
        const data = await ses.send(new GetTemplateCommand(params));
        console.log('Success. Template:', data.Template)
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.v3.templates.getTemplate]
exports.run = run; //for unit tests only
