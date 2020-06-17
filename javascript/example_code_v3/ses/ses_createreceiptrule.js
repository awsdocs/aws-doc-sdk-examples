/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-receipt-rules.html

Purpose:
ses_createreceiptrule.js demonstrates how to create a receipt rule in Amazon SES to save
received messages in an Amazon S3 bucket.

Inputs:
- REGION (in commmand line input below)
- S3_BUCKET_NAME (in commmand line input below)
- DOMAIN | EMAIL_ADDRESS (in commmand line input below): specify a domain to add all
  email addresses belonging to that domain; or specify individual email addresses.
- RULE_NAME (in commmand line input below)
- RULE_SET_NAME (in commmand line input below)

Running the code:
node ses_createreceiptrule.js REGION S3_BUCKET_NAME DOMAIN | EMAIL_ADDRESS RULE_NAME RULE_SET_NAME
*/
// snippet-start:[ses.JavaScript.rules.createReceiptRule]
async function run(){
    try{
        const { SES, SendEmailCommand } = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        // Create the SES service object
        const ses = new SES(region);
        const params = {
            Rule: {
                Actions: [
                    {
                        S3Action: {
                            BucketName: process.argv[3],
                            ObjectKeyPrefix: "email"
                        }
                    }
                ],
                Recipients: [
                    process.argv[4],
                    /* more items */
                ],
                Enabled: true | false,
                Name: process.argv[5],
                ScanEnabled: true | false,
                TlsPolicy: "Optional"
            },
            RuleSetName: process.argv[6]
        };
        const data = await ses.send(new CreateReceiptRuleCommand(params));
        console.log("Role created")
    }
    catch(err){
        console.error(err, err.stack);
    }
}
run();
// snippet-end:[ses.JavaScript.rules.createReceiptRule]
exports.run = run;
