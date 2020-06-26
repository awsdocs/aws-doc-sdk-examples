/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_deletereceiptruleset.js demonstrates how to delete an Amazon SES receipt rule set.

Inputs:
- REGION (into command line below)
- RULE_NAME (into command line below)
- RULE_SET_NAME (into command line below)

Running the code:
node ses_deletereceiptruleset.js REGION RULE_SET_NAME
 */
// snippet-start:[ses.JavaScript.v3.rules.deleteReceiptRuleSet]
// Import required AWS SDK clients and commands for Node.js
const {SES, DeleteReceiptRuleSetCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {RuleSetName: "NAME"};

async function run() {
    try {
        const data = await ses.send(new DeleteReceiptRuleSetCommand(params));
        console.log('Success, rule set deleted', data)
        } catch (err) {
        console.error(err, err.stack);
        }
};
run();
// snippet-end:[ses.JavaScript.v3.rules.deleteReceiptRuleSet]
exports.run = run; //for unit tests only
