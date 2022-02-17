/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[pinpoint_send_email_message_email_api demonstrates how to send a transactional email message by using the SendEmail operation in the Amazon Pinpoint Email API.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Amazon Pinpoint Email API]
// snippet-keyword:[Code Sample]
// snippet-keyword:[SendEmail]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.javascript.pinpoint_send_email_message_email_api.complete]

'use strict';

var AWS = require('aws-sdk');

// The AWS Region that you want to use to send the email. For a list of
// AWS Regions where the Amazon Pinpoint Email API is available, see
// https://docs.aws.amazon.com//pinpoint-email/latest/APIReference
var aws_region = "us-west-2";

// The "From" address. This address has to be verified in Amazon Pinpoint
// in the region that you use to send email.
var senderAddress = "Mary Major <sender@example.com>";

// The address on the "To" line. If your Amazon Pinpoint account is in
// the sandbox, this address also has to be verified.
// Note: All recipient addresses in this example are in arrays, which makes it
// easier to specify multiple recipients. Alternatively, you can make these
// variables strings, and then modify the To/Cc/BccAddresses attributes in the
// params variable so that it passes an array for each recipient type.
var toAddresses = [ "recipient@example.com" ];

// CC and BCC addresses. If your account is in the sandbox, these
// addresses have to be verified.
var ccAddresses = [ "cc_recipient1@example.com","cc_recipient2@example.com" ];
var bccAddresses = [ "bcc_recipient@example.com" ];

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
var tag0 = { 'Name':'key0', 'Value':'value0' };
var tag1 = { 'Name':'key1', 'Value':'value1' };

// The character encoding the you want to use for the subject line and
// message body of the email.
var charset = "UTF-8";

// Specify that you're using a shared credentials file, and specify which
// profile to use in the shared credentials file.
var credentials = new AWS.SharedIniFileCredentials({profile: 'default'});
AWS.config.credentials = credentials;

// Specify the region.
AWS.config.update({region:aws_region});

//Create a new PinpointEmail object.
var pinpointEmail = new AWS.PinpointEmail();

// Specify the parameters to pass to the API.
var params = {
  FromEmailAddress: senderAddress,
  Destination: {
    ToAddresses: toAddresses,
    CcAddresses: ccAddresses,
    BccAddresses: bccAddresses
  },
  Content: {
    Simple: {
      Body: {
        Html: {
          Data: body_html,
          Charset: charset
        },
        Text: {
          Data: body_text,
          Charset: charset
        }
      },
      Subject: {
        Data: subject,
        Charset: charset
      }
    }
  },
  ConfigurationSetName: configuration_set,
  EmailTags: [
    tag0,
    tag1
  ]
};

//Try to send the email.
pinpointEmail.sendEmail(params, function(err, data) {
  // If something goes wrong, print an error message.
  if(err) {
    console.log(err.message);
  } else {
    console.log("Email sent! Message ID: ", data.MessageId);
  }
});

// snippet-end:[pinpoint.javascript.pinpoint_send_email_message_email_api.complete]

