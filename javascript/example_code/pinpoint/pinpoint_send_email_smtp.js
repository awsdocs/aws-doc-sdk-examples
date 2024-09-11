// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*/

// snippet-start:[pinpoint.javascript.pinpoint_send_email_smtp.complete]
/*
This code uses callbacks to handle asynchronous function responses.
It currently demonstrates using an async-await pattern.
AWS supports both the async-await and promises patterns.
For more information, see the following:
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/calling-services-asynchronously.html
https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-handler.html
*/

"use strict";
const nodemailer = require("nodemailer");

// If you're using Amazon Pinpoint in a region other than US West (Oregon),
// replace email-smtp.us-west-2.amazonaws.com with the Amazon Pinpoint SMTP
// endpoint in the appropriate AWS Region.
const smtpEndpoint = "email-smtp.us-west-2.amazonaws.com";

// The port to use when connecting to the SMTP server.
const port = 587;

// Replace sender@example.com with your "From" address.
// This address must be verified with Amazon Pinpoint.
const senderAddress = "Mary Major <sender@example.com>";

// Replace recipient@example.com with a "To" address. If your account
// is still in the sandbox, this address must be verified. To specify
// multiple addresses, separate each address with a comma.
var toAddresses = "recipient@example.com";

// CC and BCC addresses. If your account is in the sandbox, these
// addresses have to be verified. To specify multiple addresses, separate
// each address with a comma.
var ccAddresses = "cc-recipient0@example.com,cc-recipient1@example.com";
var bccAddresses = "bcc-recipient@example.com";

// Replace smtp_username with your &PINlong; SMTP user name.
const smtpUsername = "AKIAIOSFODNN7EXAMPLE";

// Use &ASMlong; to expose your &PIN; SMTP password.
const smtpPassword = process.env["SMTP_PASSWORD"];

// (Optional) the name of a configuration set to use for this message.
var configurationSet = "ConfigSet";

// The subject line of the email
var subject = "Amazon Pinpoint test (Nodemailer)";

// The email body for recipients with non-HTML email clients.
var body_text = `Amazon Pinpoint Test (Nodemailer)
---------------------------------
This email was sent through the Amazon Pinpoint SMTP interface using Nodemailer.
`;

// The body of the email for recipients whose email clients support HTML content.
var body_html = `<html>
<head></head>
<body>
  <h1>Amazon Pinpoint Test (Nodemailer)</h1>
  <p>This email was sent with <a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint</a>
        using <a href='https://nodemailer.com'>Nodemailer</a> for Node.js.</p>
</body>
</html>`;

// The message tags that you want to apply to the email.
var tag0 = "key0=value0";
var tag1 = "key1=value1";

async function main() {
  // Create the SMTP transport.
  let transporter = nodemailer.createTransport({
    host: smtpEndpoint,
    port: port,
    secure: false, // true for 465, false for other ports
    auth: {
      user: smtpUsername,
      pass: smtpPassword,
    },
  });

  // Specify the fields in the email.
  let mailOptions = {
    from: senderAddress,
    to: toAddresses,
    subject: subject,
    cc: ccAddresses,
    bcc: bccAddresses,
    text: body_text,
    html: body_html,
    // Custom headers for configuration set and message tags.
    headers: {
      "X-SES-CONFIGURATION-SET": configurationSet,
      "X-SES-MESSAGE-TAGS": tag0,
      "X-SES-MESSAGE-TAGS": tag1,
    },
  };

  // Send the email.
  let info = await transporter.sendMail(mailOptions);

  console.log("Message sent! Message ID: ", info.messageId);
}

main().catch(console.error);

// snippet-end:[pinpoint.javascript.pinpoint_send_email_smtp.complete]
