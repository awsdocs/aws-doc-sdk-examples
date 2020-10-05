/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_createreceiptruleset.ts demonstrates how to create an empty AWS SES rule set.

Inputs (replace in code):
- REGION
- RULE_SET_NAME

Running the code:
ts-node ses_createreceiptruleset.ts
*/
// snippet-start:[ses.JavaScript.rules.createReceiptRuleSetV3]
// Import required AWS SDK clients and commands for Node.js
const {
  SESClient,
  CreateReceiptRuleSetCommand
} = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1" // REGION

// Set the parameters
const params = { RuleSetName: "RULE_SET_NAME" }; //RULE_SET_NAME

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new CreateReceiptRuleSetCommand(params));
    console.log(
      "Success, receipt rule created; requestId",
      data.$metadata.requestId
    );
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.rules.createReceiptRuleSetV3]

