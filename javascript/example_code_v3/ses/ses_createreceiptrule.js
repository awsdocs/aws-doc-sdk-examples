/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_createreceiptrule.js demonstrates how to create a receipt rule in Amazon SES to save
received messages in an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- S3_BUCKET_NAME
- EMAIL_ADDRESS | DOMAIN
- RULE_NAME
- RULE_SET_NAME

Running the code:
node ses_createreceiptrule.js
*/
// snippet-start:[ses.JavaScript.rules.createReceiptRuleV3]

// Import required AWS SDK clients and commands for Node.js
const { SES, CreateReceiptRuleCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1" // REGION

// Set the parameters
const params = {
  Rule: {
    Actions: [
      {
        S3Action: {
          BucketName: "S3_BUCKET_NAME", // S3_BUCKET_NAME
          ObjectKeyPrefix: "email",
        },
      },
    ],
    Recipients: [
      "EMAIL_ADDRESS", // (in command line below; specify a domain to add all email addresses belonging
      // to that domain, or specify individual email addresses)
      /* more items */
    ],
    Enabled: true | false,
    Name: "RULE_NAME", // RULE_NAME
    ScanEnabled: true | false,
    TlsPolicy: "Optional",
  },
  RuleSetName: "RULE_SET_NAME", // RULE_SET_NAME
};

// Create SES service object
const ses = new SES(REGION);

const run = async () => {
  try {
    const data = await ses.send(new CreateReceiptRuleCommand(params));
    console.log("Rule created; requestId:", data.$metadata.requestId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.rules.createReceiptRuleV3]
exports.run = run; //for unit tests only
