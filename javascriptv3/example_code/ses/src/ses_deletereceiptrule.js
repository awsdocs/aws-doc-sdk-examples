/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' atic
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_deletereceiptrule.js demonstrates how to delete an Amazon SES receipt rule.

Inputs (replace in code):
- RULE_NAME
- RULE_SET_NAME

Running the code:
node ses_deletereceiptrule.js
 */

// snippet-start:[ses.JavaScript.rules.deleteReceiptRuleV3]
// Import required AWS SDK clients and commands for Node.js
import { DeleteReceiptRuleCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";
// Set the deleteReceiptRule params
var params = {
  RuleName: "RULE_NAME", // RULE_NAME
  RuleSetName: "RULE_SET_NAME", // RULE_SET_NAME
};

const run = async () => {
  try {
    const data = await sesClient.send(new DeleteReceiptRuleCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.rules.deleteReceiptRuleV3]
// For unit tests only.
export { run, params }
