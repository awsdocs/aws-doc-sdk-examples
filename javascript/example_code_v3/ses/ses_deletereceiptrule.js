/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-receipt-rules.html

Purpose:
ses_deletereceiptrule.test.js demonstrates how to delete an Amazon SES receipt rule.

Inputs:
- REGION (in commmand line input below)
- RULE_NAME (in commmand line input below)
- RULE_SET_NAME (in commmand line input below)

Running the code:
node ses_deletereceiptrule.test.js REGION RULE_NAME RULE_SET_NAME
 */

// snippet-start:[ses.JavaScript.rules.deleteReceiptRule]
async function run() {
  try {
    const {SES, DeleteReceiptFilterCommand} = require("@aws-sdk/client-ses");
    const region = process.argv[2];
    const ses = new SES(region);
    // Create deleteReceiptRule params
    var params = {
      RuleName: process.argv[3], /* required */
      RuleSetName: process.argv[4] /* required */
    };
    const data = await ses.send(new DeleteReceiptFilterCommand(params));
    console.log("Receipt Rule Deleted")
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.rules.deleteReceiptRule]
exports.run = run;
