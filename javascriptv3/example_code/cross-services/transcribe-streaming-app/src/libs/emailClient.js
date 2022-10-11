/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
This file handles the sending of the translated transcription using
AWS SES 
*/
import { SESClient } from "@aws-sdk/client-ses";
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import * as awsID from "./awsID.js";

export const sendEmail = async(sender, receiver, originalText, translatedText) => {
  const sesClient = createSESClient();
  const htmlBody = createHTMLBody(originalText, translatedText);
  const textBody = createTextBody(originalText, translatedText);
  const command = createEmailCommand(sender, receiver, htmlBody, textBody);
  await sesClient.send(new SendEmailCommand(command));
}

const createSESClient = () => {
  return new SESClient({
    region: awsID.REGION,
    credentials: fromCognitoIdentityPool({
      client: new CognitoIdentityClient({ region: awsID.REGION }),
      identityPoolId: awsID.IDENTITY_POOL_ID,
    }),
  });
}

const createHTMLBody = (originalText, translatedText) => {
  return {
    Charset: "UTF-8",
    Data:
      "<h1>Hello!</h1><p>Here is your Amazon Transcribe recording:</p>" +
      "<h1>Original</h1>" +
      "<p>" +
      originalText +
      "</p>" +
      "<h1>Translation (if available)</h1>" +
      "<p>" +
      translatedText +
      "</p>",
  }
}

const createTextBody = (originalText, translatedText) => {
  return {
    Charset: "UTF-8",
    Data:
      "Hello,\\r\\n" +
      "Here is your Amazon Transcribe transcription:" +
      "\n" +
      translatedText,
  }
}

const createEmailCommand = (sender, receiver, htmlBody, textBody) => {
  return {
    Destination: {
        CcAddresses: [
        ],
        ToAddresses: [
          receiver,
        ],
    },
    Message: {
        Body: {
          Html: htmlBody,
          Text: textBody,
        },
        Subject: {
          Charset: "UTF-8",
          Data: "Your Amazon Transcribe transcription.",
        },
      },
      Source: sender,
  };
}

