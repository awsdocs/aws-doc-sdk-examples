/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
pinpoint_send_sms_message.ts demonstrates how to send a transactional email message using Amazon Pinpoint.]

Inputs (replace in code):
- REGION: The AWS Region
- SENDER_NUMBER
- RECEIVER_NUMBER
- PINPOINT_PROJECT_ID

Running the code:
ts-node pinpoint_send_sms_message.ts
*/
// snippet-start:[pinpoint.javascript.pinpoint_send_sms_message_V3]

// Import required AWS SDK clients and commands for Node.js
const {
  PinpointClient,
  SendMessagesCommand,
} = require("@aws-sdk/client-pinpoint");

("use strict");

/*The AWS Region that you want to use to send the message. For a list of
AWS Regions where the Amazon Pinpoint API is available, see
 https://docs.aws.amazon.com/pinpoint/latest/apireference/.*/
const REGION = "REGION";

/* The phone number or short code to send the message from. The phone number
 or short code that you specify has to be associated with your Amazon Pinpoint
account. For best results, specify long codes in E.164 format. */
const originationNumber = "SENDER_NUMBER"; //e.g., +1XXXXXXXXXX

// The recipient's phone number.  For best results, you should specify the phone number in E.164 format.
const destinationNumber = "RECEIVER_NUMBER"; //e.g., +1XXXXXXXXXX

// The content of the SMS message.
const message =
  "This message was sent through Amazon Pinpoint " +
  "using the AWS SDK for JavaScript in Node.js. Reply STOP to " +
  "opt out.";

/*The Amazon Pinpoint project/application ID to use when you send this message.
Make sure that the SMS channel is enabled for the project or application
that you choose.*/
const projectId = "PINPOINT_PROJECT_ID"; //e.g., XXXXXXXX66e4e9986478cXXXXXXXXX

/* The type of SMS message that you want to send. If you plan to send
time-sensitive content, specify TRANSACTIONAL. If you plan to send
marketing-related content, specify PROMOTIONAL.*/
var messageType = "TRANSACTIONAL";

// The registered keyword associated with the originating short code.
var registeredKeyword = "myKeyword";

/* The sender ID to use when sending the message. Support for sender ID
// varies by country or region. For more information, see
https://docs.aws.amazon.com/pinpoint/latest/userguide/channels-sms-countries.html.*/

var senderId = "MySenderID";

// Specify the parameters to pass to the API.
var params = {
  ApplicationId: projectId,
  MessageRequest: {
    Addresses: {
      [destinationNumber]: {
        ChannelType: "SMS",
      },
    },
    MessageConfiguration: {
      SMSMessage: {
        Body: message,
        Keyword: registeredKeyword,
        MessageType: messageType,
        OriginationNumber: originationNumber,
        SenderId: senderId,
      },
    },
  },
};

//Create a new Pinpoint client object.
const pinpointClient = new PinpointClient({ region: REGION });

const run = async () => {
  try {
    const data = await pinpointClient.send(new SendMessagesCommand(params));
    console.log(
      "Message sent! " +
        data["MessageResponse"]["Result"][destinationNumber]["StatusMessage"]
    );
  } catch (err) {
    console.log(err.message);
  }
};
run();
// snippet-end:[pinpoint.javascript.pinpoint_send_sms_message_V3]
