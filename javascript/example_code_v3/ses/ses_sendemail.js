/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html.

Purpose:
ses_sendemail.js demonstrates how to send an email using SES.

Inputs:
- REGION (in commmand line input below)
- RECEIVER_ADDRESS (in commmand line input below)
- SENDER_ADDRESS (in commmand line input below)
- TEXT_FORMAT_BODY (replace in code): Body content of email.
- EMAIL_SUBJECT (replace in code): Subject of email.
- CcAddresses (replace in code; optional): Additional receiver addressses.
- ReplyToAddresses (replace in code; optional): Additional addresses automatically added to replys.

Running the code:
node ses_sendemail.js REGION RECEIVER_ADDRESS SENDER_ADDRESS

// snippet-start:[ses.JavaScript.email.sendEmail]
*/
// Create the promise and SES service object
async function run(){
    try{
        const { SES, SendEmailCommand } = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        const ses = new SES(region);
        const params = {
            Destination: { /* required */
                CcAddresses: [

                    /* more items */
                ],
                ToAddresses: [
                    process.argv[3]
                    /* more To-email addresses */
                ]
            },
            Message: { /* required */
                Body: { /* required */
                    Html: {
                        Charset: "UTF-8",
                        Data: "HTML_FORMAT_BODY"
                    },
                    Text: {
                        Charset: "UTF-8",
                        Data: "TEXT_FORMAT_BODY"
                    }
                },
                Subject: {
                    Charset: 'UTF-8',
                    Data: 'EMAIL_SUBJECT'
                }
            },
            Source: process.argv[4], /* required */
            ReplyToAddresses: [
                /* more items */
            ],
        };
        const data = await ses.send(new SendEmailCommand(params));
        console.log('Success', data)
    }
    catch(err){
        console.log('Error', err);
    }
}
run();
// snippet-end:[ses.JavaScript.email.sendEmail]
exports.run = run;
