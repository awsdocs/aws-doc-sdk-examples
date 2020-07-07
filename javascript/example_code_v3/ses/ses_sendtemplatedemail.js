/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ses-examples-sending-email.html.

Purpose:
ses_sendtemplatedemail.js demonstrates how to compose an Amazon SES templated email and
queue it for sending.

Inputs:
- REGION (into command line below)
- RECEIVER_ADDRESS (into command line below)
- SENDER_ADDRESS (into command line below)
- TEXT_FORMAT_BODY (replace in code; body content of email)
- EMAIL_SUBJECT (replace in code; subject of email)
- CcAddresses (replace in code; additional receiver addresses - optional)
- ReplyToAddresses (replace in code; additional addresses automatically added to replies - optional)

Running the code:
node ses_sendtemplatedemail.js REGION RECEIVER_ADDRESS SENDER_ADDRESS TEMPLATE_NAME
 */
// snippet-start:[ses.JavaScript.email.sendTemplatedEmailV3]
// Import required AWS SDK clients and commands for Node.js
const {SES, SendTemplatedEmailCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {
  Destination: { /* required */
    CcAddresses: [

      /* more CC email addresses */
    ],
    ToAddresses: [
      process.argv[3]
      /* more To-email addresses */
    ]
  },
  Source: process.argv[4], /* required */
  Template: process.argv[5], /* required */
  TemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }', /* required */
  ReplyToAddresses: [
  ],
};

async function run() {
  try {
    const data = await ses.send(new SendTemplatedEmailCommand(params));
    console.log('Success, templated email sent; messageId:', data.MessageId)
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.email.sendTemplatedEmailV3]
exports.run = run; //for unit tests only
