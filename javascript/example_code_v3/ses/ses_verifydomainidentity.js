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
ses_verifydomainidentity.js demonstrates how to add a domain to the list of Amazon SES identities and attempts to verify it.

Inputs:
- REGION (in commmand line input below)
- RULE_NAME (in commmand line input below)
- RULE_SET_NAME (in commmand line input below)

Running the code:
node ses_verifydomainidentity.js REGION DOMAIN
 */
// snippet-start:[ses.JavaScript.identities.verifyDomainIdentity]
async function run() {
    try {
        const {SES, VerifyDomainIdentityCommand} = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        const ses = new SES(region);
        const params = {Domain: process.argv[3]};
        // Create deleteReceiptRule params
        const data = await ses.send(new VerifyDomainIdentityCommand(params));
        console.log("Verification Token: " + data.VerificationToken)
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.identities.verifyDomainIdentity]
exports.run = run;
