/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-receipt-rules.html.

Purpose:
ses_createreceiptrule.js demonstrates how to create a receipt rule in Amazon SES to save
received messages in an Amazon S3 bucket.

Inputs:
- REGION (into command line below)
- S3_BUCKET_NAME (into command line below)
- DOMAIN | EMAIL_ADDRESS (in command line below; specify a domain to add all
  email addresses belonging to that domain, or specify individual email addresses)
- RULE_NAME (into command line below)
- RULE_SET_NAME (into command line below)

Running the code:
node ses_createreceiptrule.js REGION S3_BUCKET_NAME DOMAIN | EMAIL_ADDRESS RULE_NAME RULE_SET_NAME
*/
// snippet-start:[ses.JavaScript.v3.rules.createReceiptRule]
// Import required AWS SDK clients and commands for Node.js
const { SES, CreateReceiptRuleCommand } = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2]; // REGION
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {
    Rule: {
        Actions: [
            {
                S3Action: {
                    BucketName: process.argv[3], // S3_BUCKET_NAME
                    ObjectKeyPrefix: "email"
                }
            }
        ],
        Recipients: [
            process.argv[4], // DOMAIN | EMAIL_ADDRESS
            /* more items */
        ],
        Enabled: true | false,
        Name: process.argv[5], // RULE_NAME
        ScanEnabled: true | false,
        TlsPolicy: "Optional"
    },
    RuleSetName: process.argv[6] // RULE_SET_NAME
};

async function run(){
    try{
        const data = await ses.send(new CreateReceiptRuleCommand(params));
        console.log("Rule created; requestId:", data.$metadata.requestId);
    }
    catch(err){
        console.error(err, err.stack);
    }
}
run();
// snippet-end:[ses.JavaScript.v3.rules.createReceiptRule]
exports.run = run; //for unit tests only
