/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ses-examples-sending-email.html.

Purpose:
ses_sendbulktemplatedemail.js demonstrates how to compose an Amazon SES templated email for
multiple destinations and queue it for sending.

Inputs (replace in code):
- RECEIVER_ADDRESSES
- SENDER_ADDRESS


Running the code:
node ses_sendbulktemplatedemail.js
 */
// snippet-start:[ses.JavaScript.email.sendBulkTemplatedEmailV3]
// Import required AWS SDK clients and commands for Node.js
import {
  SendBulkTemplatedEmailCommand
} from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

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

const run = async () => {
  try {
    const data = await sesClient.send(new SendBulkTemplatedEmailCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.email.sendBulkTemplatedEmailV3]
// For unit tests only.
// module.exports ={run, params};
