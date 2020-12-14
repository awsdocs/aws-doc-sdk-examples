/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//sns-examples-sending-sms.html.

Purpose:
sns_listnumbersoptedout.ts demonstrates how to retrieve a list of phone numbers that have opted out of receiving Amazon SMS messages.

Inputs (replace in code):
- REGION

Running the code:
ts-node sns_listnumbersoptedout.ts
 */
// snippet-start:[sns.JavaScript.SMS.listPhoneNumbersOptedOutV3]

// Import required AWS SDK clients and commands for Node.js
const { SNSClient, ListPhoneNumbersOptedOutCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create SNS service object
const sns = new SNSClient({ region: REGION });

const run = async () => {
  try {
    const data = await sns.send(new ListPhoneNumbersOptedOutCommand({}));
    console.log("Success. Opted-out phone numbers:", data.phoneNumbers);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.SMS.listPhoneNumbersOptedOutV3]

