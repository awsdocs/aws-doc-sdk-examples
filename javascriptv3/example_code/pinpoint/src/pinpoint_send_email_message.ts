/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3.

Purpose:
pinpoint_send_email_message.ts demonstrates how to send a transactional email message using Amazon Pinpoint.

Inputs (replace in code):
- REGION: The AWS Region
- SENDER_ADDRESS
- RECIPIENT_ADDRESS
- PINPOINT_PROJECT_ID
- CC_ADDRESSES (optional)
- BCC_ADDRESSES (optional)

Running the code:
ts-node pinpoint_send_email_message.ts
*/
// snippet-start:[pinpoint.javascript.pinpoint_send_email_message_v3]

// Import required AWS SDK clients and commands for Node.js
const {
  PinpointClient,
  SendMessagesCommand,
} = require("@aws-sdk/client-pinpoint");

("use strict");

// Set the AWS Region
const REGION = "REGION";

/* The address on the "To" line. If your Amazon Pinpoint account is in
the sandbox, this address also has to be verified.
Note: All recipient addresses in this example are in arrays, which makes it
easier to specify multiple recipients. Alternatively, you can make these
variables strings, and then modify the To/Cc/BccAddresses attributes in the
params variable so that it passes an array for each recipient type.*/
const senderAddress = "SENDER_ADDRESS";
const toAddress = "RECIPIENT_ADDRESS";
const projectId = "PINPOINT_PROJECT_ID"; //e.g., XXXXXXXX66e4e9986478cXXXXXXXXX

// CC and BCC addresses. If your account is in the sandbox, these addresses have to be verified.
var ccAddresses = ["cc_recipient1@example.com", "cc_recipient2@example.com"];
var bccAddresses = ["bcc_recipient@example.com"];

// The configuration set that you want to use to send the email.
var configuration_set = "ConfigSet";

// The subject line of the email.
var subject = "Amazon Pinpoint Test (AWS SDK for JavaScript in Node.js)";

// The email body for recipients with non-HTML email clients.
var body_text = `Amazon Pinpoint Test (SDK for JavaScript in Node.js)
----------------------------------------------------
This email was sent with Amazon Pinpoint using the AWS SDK for JavaScript in Node.js.
For more information, see https:\/\/aws.amazon.com/sdk-for-node-js/`;

// The body of the email for recipients whose email clients support HTML content.
var body_html = `<html>
<head></head>
<body>
  <h1>Amazon Pinpoint Test (SDK for JavaScript in Node.js)</h1>
  <p>This email was sent with
    <a href='https://aws.amazon.com//pinpoint/'>the Amazon Pinpoint Email API</a> using the
    <a href='https://aws.amazon.com//sdk-for-node-js/'>
      AWS SDK for JavaScript in Node.js</a>.</p>
</body>
</html>`;

// The message tags that you want to apply to the email.
var tag0 = { Name: "key0", Value: "value0" };
var tag1 = { Name: "key1", Value: "value1" };

// The character encoding for the subject line and message body of the email.
var charset = "UTF-8";

const params = {
  ApplicationId: projectId,
  MessageRequest: {
    Addresses: {
      Destination: {
        ToAddresses: toAddress,
        //  CcAddresses: CC_ADDRESSES,
        //  BccAddresses: BCC_ADDRESSES
      },

      [toAddress]: {
        ChannelType: "EMAIL",
      },
    },
    MessageConfiguration: {
      EmailMessage: {
        FromAddress: senderAddress,
        SimpleEmail: {
          Subject: {
            Charset: charset,
            Data: subject,
          },
          HtmlPart: {
            Charset: charset,
            Data: body_html,
          },
          TextPart: {
            Charset: charset,
            Data: body_text,
          },
        },
      },
    },
  },
};

// Create a new Pinpoint client object.
const pinpointClient = new PinpointClient(REGION);

const run = async () => {
  try {
    const data = await pinpointClient.send(new SendMessagesCommand(params));
    console.log(
      "Email sent! Message ID: ",
      data["MessageResponse"]["Result"][toAddress]["MessageId"]
    );
  } catch (err) {
    console.log("Error", err.message);
  }
};
run();
// snippet-end:[pinpoint.javascript.pinpoint_send_email_message_v3]
