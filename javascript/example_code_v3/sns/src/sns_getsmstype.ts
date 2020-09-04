/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
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
const { SNS, GetSMSAttributesCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
var params = {
    attributes: [
        'DefaultSMSType',
        'ATTRIBUTE_NAME'
        /* more items */
    ]
};

// Create SNS service object
const sns = new SNS(REGION);

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
module.exports = {run}; //for unit tests only

