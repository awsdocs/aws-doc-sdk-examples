/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted atic
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_deletereceiptrule.test.js demonstrates how to delete an Amazon SES receipt rule.

Inputs:
- REGION (into command line below)
- RULE_NAME (into command line below)
- RULE_SET_NAME (into command line below)

Running the code:
node ses_deletereceiptrule.js REGION RULE_NAME RULE_SET_NAME
 */

// snippet-start:[ses.JavaScript.rules.deleteReceiptRuleV3]
// Import required AWS SDK clients and commands for Node.js
const {SES, DeleteReceiptRuleCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the deleteReceiptRule params
var params = {
  RuleName: process.argv[3], /* required */
  RuleSetName: process.argv[4] /* required */
};

async function run() {
  try {
    const data = await ses.send(new DeleteReceiptRuleCommand(params));
    console.log("Receipt Rule Deleted")
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.rules.deleteReceiptRuleV3]
exports.run = run; //for unit tests only
