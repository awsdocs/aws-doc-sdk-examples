/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) top
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_verifyemailidentity.js demonstrates how to send an Amazon SES verification email.]

Inputs:
- REGION (into command line below)
- ADDRESS@DOMAIN.EXT (into command line below; e.g., name@example.com.)

Running the code:
node ses_verifyemailidentity.js REGION ADDRESS@DOMAIN.EXT

 */
// snippet-start:[ses.JavaScript.v3.identities.verifyEmailIdentity]
// Import required AWS SDK clients and commands for Node.js
const {SES, VerifyEmailIdentityCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {EmailAddress: process.argv[3]};

async function run() {
    try {
        const data = await ses.send(new VerifyEmailIdentityCommand(params));
        console.log("Email verification initiated")
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.v3.identities.verifyEmailIdentity]
exports.run = run; //for unit tests only
