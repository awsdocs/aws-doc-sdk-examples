/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) top
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-managing-identities.html.

Purpose:
ses_deleteidentity.js demonstrates how to delete an Amazon SES identity.

Inputs:
- REGION (into command line below)
- IDENTITY_TYPE (into command line; 'EmailAddress' or 'Domain')
- IDENTITY_NAME

Running the code:
node ses_deleteidentity.js REGION IDENTITY_TYPE IDENTITY_NAME
*/
// snippet-start:[ses.JavaScript.v3.identities.deleteIdentity]
// Import required AWS SDK clients and commands for Node.js
const {SES, DeleteIdentityCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {IdentityType: process.argv[3], // IDENTITY_TYPE
                Identity: process.argv[4]}; // IDENTITY_NAME

async function run() {
    try {
        const data = await ses.send(new DeleteIdentityCommand(params));
        console.log("Identity Deleted")
        } catch (err) {
        console.error(err, err.stack);
        }
};
run()
// snippet-end:[ses.JavaScript.v3.identities.deleteIdentity]
exports.run = run; //for unit tests only
