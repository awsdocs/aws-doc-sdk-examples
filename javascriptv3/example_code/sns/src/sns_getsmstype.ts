/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//sns-examples-sending-sms.html.

Purpose:
sns_getsmstype.ts demonstrates how to retrieve the settings for sending Amazon SMS messages.

Inputs (replace in code):
- REGION

Running the code:
ts-node sns_getsmstype.ts
*/

// snippet-start:[sns.JavaScript.SMS.getSMSAttributesV3]
// Import required AWS SDK clients and commands for Node.js
const { SNSClient, GetSMSAttributesCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
var params = {
  attributes: [
    "DefaultSMSType",
    "ATTRIBUTE_NAME",
    /* more items */
  ],
};

// Create SNS service object
const sns = new SNSClient(REGION);

const run = async () => {
  try {
    const data = await sns.send(new GetSMSAttributesCommand(params));
    console.log("RequestId:", data.$metadata.requestId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.SMS.getSMSAttributesV3]

