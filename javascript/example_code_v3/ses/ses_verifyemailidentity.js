/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-managing-identities.html

Purpose:
ses_verifyemailidentity.js demonstrates how to send an Amazon SES verification email.]

Inputs:
- REGION (in commmand line input below)
- ADDRESS@DOMAIN.EXT (in commmand line input below) e.g. name@example.com

Running the code:
node ses_verifyemailidentity.js REGION ADDRESS@DOMAIN.EXT

 */
// snippet-start:[ses.JavaScript.identities.verifyEmailIdentity]

async function run() {
    try {
        const {SES, VerifyEmailIdentityCommand} = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        const ses = new SES(region);
        const params = {EmailAddress: process.argv[3]};
        // Create deleteReceiptRule params
        const data = await ses.send(new VerifyEmailIdentityCommand(params));
        console.log("Email verification initiated")
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.identities.verifyEmailIdentity]
exports.run = run;
