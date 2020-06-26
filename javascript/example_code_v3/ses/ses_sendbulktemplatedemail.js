/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) top
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ses-examples-sending-email.html.

Purpose:
ses_sendbulktemplatedemail.js demonstrates how to compose an Amazon SES templated email for
multiple destinations and queue it for sending.

Inputs:
- REGION (into command line below)
- RECEIVER_ADDRESS (into command line below)
- SENDER_ADDRESS (into command line below)
- TEXT_FORMAT_BODY (replace in code; body content of email.)
- EMAIL_SUBJECT (replace in code; subject of email.)
- CcAddresses (replace in code; additional receiver addressses - optional.)
- ReplyToAddresses (replace in code; additional addresses automatically added to replies - optional.)

Running the code:
node ses_sendbulktemplatedemail.js REGION RECEIVER_ADDRESS SENDER_ADDRESS TEMPLATE
 */
// snippet-start:[ses.JavaScript.v3.email.sendBulkTemplatedEmail]
// Import required AWS SDK clients and commands for Node.js
const {SES, SendBulkTemplatedEmailCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
var params = {
  Destinations: [ /* required */
    {
      Destination: { /* required */
        CcAddresses: [
          process.argv[3],
          /* more items */
        ],
        ToAddresses: [
          /* more items */
        ]
      },
      ReplacementTemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }'
    },
  ],
  Source: process.argv[4], /* required */
  Template: process.argv[5], /* required */
  DefaultTemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }',
  ReplyToAddresses: [

  ]
};

async function run() {
  try {
    const data = await ses.send(new SendBulkTemplatedEmailCommand(params));
    console.log(data)
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.v3.email.sendBulkTemplatedEmail]
exports.run = run; //for unit tests only
