/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//sns-examples-sending-sms.html

Purpose:
sns_setsmstype.js demonstrates how to use Amazon SNS to set default SMS attributes.

Inputs:
- REGION (in commmand line input below)
- DefaultSMSType (in code)

Running the code:
node sns_setsmstype.js REGION
*/

// snippet-start:[sns.JavaScript.SMS.setSMSAttributes]
async function run() {
  try {
    const {SNS, SetSMSAttributesCommand} = require("@aws-sdk/client-sns");
    const region = process.argv[2];
    const sns = new SNS(region);
    const params = {
      attributes: { /* required */
        'DefaultSMSType': 'Transactional', /* highest reliability */
        //'DefaultSMSType': 'Promotional' /* lowest cost */
      }
    };
    const data = await sns.send(new SetSMSAttributesCommand(params));
    console.log("Success, " + data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.SMS.setSMSAttributes]
exports.run = run;
