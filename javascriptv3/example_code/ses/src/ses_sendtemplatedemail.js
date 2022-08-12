/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ses-examples-sending-email.html.

Purpose:
ses_sendtemplatedemail.js demonstrates how to compose an Amazon SES templated email and
queue it for sending.

Inputs (replace in code):
- RECEIVER_ADDRESS
- SENDER_ADDRESS
- TEMPLATE_NAME

Running the code:
node ses_sendtemplatedemail.js
 */
// snippet-start:[ses.JavaScript.email.sendTemplatedEmailV3]

// Import required AWS SDK clients and commands for Node.js
import { SendTemplatedEmailCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

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

const run = async () => {
  try {
    const data = await sesClient.send(new SendTemplatedEmailCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.email.sendTemplatedEmailV3]
// For unit tests only.
export { run, params }
