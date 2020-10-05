/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ses-examples-sending-email.html.

Purpose:
ses_sendbulktemplatedemail.ts demonstrates how to compose an Amazon SES templated email for
multiple destinations and queue it for sending.

Inputs (replace in code):
- REGION
- RECEIVER_ADDRESSES
- SENDER_ADDRESS


Running the code:
ts-node ses_sendbulktemplatedemail.ts
 */
// snippet-start:[ses.JavaScript.email.sendBulkTemplatedEmailV3]
// Import required AWS SDK clients and commands for Node.js
const {
  SESClient,
  SendBulkTemplatedEmailCommand
} = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
var params = {
  Destinations: [
    /* required */
    {
      Destination: {
        /* required */
        CcAddresses: [
          "RECEIVER_ADDRESSES", //RECEIVER_ADDRESS
          /* more items */
        ],
        ToAddresses: [
          /* more items */
        ],
      },
      ReplacementTemplateData: '{ "REPLACEMENT_TAG_NAME":"REPLACEMENT_VALUE" }',
    },
  ],
  Source: "SENDER_ADDRESS", // SENDER_ADDRESS
  Template: "TEMPLATE", //TEMPLATE
  DefaultTemplateData: '{ "REPLACEMENT_TAG_NAME":"REPLACEMENT_VALUE" }',
  ReplyToAddresses: [],
};

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new SendBulkTemplatedEmailCommand(params));
    console.log(data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.email.sendBulkTemplatedEmailV3]

