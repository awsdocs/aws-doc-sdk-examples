/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_listidentities.js demonstrates how to list all the identities for an AWS account.

Inputs:
- REGION (into command line below)
- IDENTITY_TYPE (into command line; 'EmailAddress' or 'Domain')

Running the code:
node ses_listidentities.js  REGION IDENTITY_TYPE
*/
// snippet-start:[ses.JavaScript.v3.identities.listIdentities]
// Import required AWS SDK clients and commands for Node.js
const {SES, ListIdentitiesCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
var params = {
    IdentityType: process.argv[3], // IDENTITY_TYPE: "EmailAddress' or 'Domain'
    MaxItems: 10
};

async function run() {
    try {
        const data = await ses.send(new ListIdentitiesCommand(params));
        console.log("Success. Your SES" +process.argv[3]+ "identities:", data);
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.v3.identities.listIdentities]
exports.run = run; //for unit tests only

