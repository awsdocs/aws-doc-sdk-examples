/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ses-examples-sending-email.html.

Purpose:
ses_sendtemplatedemail.ts demonstrates how to compose an Amazon SES templated email and
queue it for sending.

Inputs (replace in code):
- REGION
- RECEIVER_ADDRESS
- SENDER_ADDRESS
- TEMPLATE_NAME

Running the code:
ts-node ses_sendtemplatedemail.ts
 */
// snippet-start:[ses.JavaScript.email.sendTemplatedEmailV3]

// Import required AWS SDK clients and commands for Node.js
const { SESClient, SendTemplatedEmailCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Destination: {
    /* required */
    CcAddresses: [
      /* more CC email addresses */
    ],
    ToAddresses: [
      "RECEIVER_ADDRESS", // RECEIVER_ADDRESS
      /* more To-email addresses */
    ],
  },
  Source: "SENDER_ADDRESS", //SENDER_ADDRESS
  Template: "TEMPLATE_NAME", // TEMPLATE_NAME
  TemplateData: '{ "REPLACEMENT_TAG_NAME":"REPLACEMENT_VALUE" }' /* required */,
  ReplyToAddresses: [],
};

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new SendTemplatedEmailCommand(params));
    console.log("Success, templated email sent; messageId:", data.MessageId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.email.sendTemplatedEmailV3]

