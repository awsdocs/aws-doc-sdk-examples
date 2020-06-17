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
ses_createreceiptruleset.js demonstrates how to create an empty Amazon SES rule set.

Inputs:
- REGION (in commmand line input below)
- RULE_SET_NAME

Running the code:
node ses_createreceiptruleset.js REGION
*/
// snippet-start:[ses.JavaScript.rules.createReceiptRuleSet]
async function run(){
    try{
        const { SES, CreateReceiptRuleSetCommand } = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        // Create the SES service object
        const ses = new SES(region);
        const params = {RuleSetName: process.argv[3]}
        const data = await ses.send(new CreateReceiptRuleSetCommand(params));
        console.log(data)
    }
    catch(err){
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.rules.createReceiptRuleSet]
exports.run = run;
