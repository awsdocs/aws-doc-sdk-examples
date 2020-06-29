/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK, 
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//sns-examples-sending-sms.html.

Purpose:
sns_setsmstype.js demonstrates how to use Amazon SNS to set default SMS attributes.

Inputs:
- REGION (into command line below)

Running the code:
node sns_setsmstype.js REGION
*/

// snippet-start:[sns.JavaScript.v3.SMS.setSMSAttributes]
// Import required AWS SDK clients and commands for Node.js
const {SNS, SetSMSAttributesCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {
  attributes: { /* required */
    'DefaultSMSType': 'Transactional', /* highest reliability */
    //'DefaultSMSType': 'Promotional' /* lowest cost */
  }
};

async function run() {
  try {
    const data = await sns.send(new SetSMSAttributesCommand(params));
    console.log("RequestId:", data.$metadata.requestId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.v3.SMS.setSMSAttributes]
exports.run = run; //for unit tests only
