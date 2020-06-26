/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//sns-examples-sending-sms.html.

Purpose:
sns_checkphoneoptout.test.js demonstrates how to determine whether a phone number has opted out of receiving Amazon SMS messages.]

Inputs:
- REGION (into command line below)
- PHONE_NUMBER  (into command line below)

Running the code:
node sns_checkphoneoptout.js REGION PHONE_NUMBER
 */
// snippet-start:[sns.JavaScript.v3.SMS.checkIfPhoneNumberIsOptedOut]

// Import required AWS SDK clients and commands for Node.js
const {SNS, CheckIfPhoneNumberIsOptedOutCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {phoneNumber: process.argv[3]}

async function run() {
    try {
        const data = await sns.send(new CheckIfPhoneNumberIsOptedOutCommand(params));
        console.log("Phone Opt Out is " + data.isOptedOut);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[sns.JavaScript.v3.SMS.checkIfPhoneNumberIsOptedOut]
exports.run = run; //for unit tests only
