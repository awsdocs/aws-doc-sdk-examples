/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript (v3) Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_deletereceiptrule.js demonstrates how to delete an Amazon SES receipt rule.

Running the code:
node ses_deletereceiptrule.js
 */

// snippet-start:[ses.JavaScript.rules.deleteReceiptRuleV3]
import { DeleteReceiptRuleCommand }  from "@aws-sdk/client-ses";
import { getUniqueName } from '../../libs/index';
import { sesClient } from "./libs/sesClient.js";

const RULE_NAME = getUniqueName('RuleName');
const RULE_SET_NAME = getUniqueName('RuleSetName');

const createDeleteReceiptRuleCommand = () => {
  return new DeleteReceiptRuleCommand({ RuleName: RULE_NAME, RuleSetName: RULE_SET_NAME });
}

const run = async () => {
  const deleteReceiptRuleCommand = createDeleteReceiptRuleCommand();
  try {
    return await sesClient.send(deleteReceiptRuleCommand);
  } catch (err) {
    console.log("Failed to delete receipt rule.", err);
    return err;
  }
};
// snippet-end:[ses.JavaScript.rules.deleteReceiptRuleV3]
export { run, RULE_NAME, RULE_SET_NAME }
