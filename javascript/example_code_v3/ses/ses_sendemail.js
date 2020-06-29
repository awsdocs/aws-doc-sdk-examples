/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
ses_sendemail.js demonstrates how to send an email using AWS SES.

Inputs:
- REGION (into command line below)
- RECEIVER_ADDRESS (into command line below)
- SENDER_ADDRESS (into command line below)
- TEXT_FORMAT_BODY (replace in code; body content of email)
- EMAIL_SUBJECT (replace in code; subject of email)
- CcAddresses (replace in code; additional receiver addresses - optional)
- ReplyToAddresses (replace in code; additional addresses automatically added to replies - optional)

Running the code:
node ses_sendemail.js REGION RECEIVER_ADDRESS SENDER_ADDRESS

// snippet-start:[ses.JavaScript.v3.email.sendEmail]
*/
// Create the promise and SES service object

// Import required AWS SDK clients and commands for Node.js
const { SES, SendEmailCommand } = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
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

async function run(){
    try{
        const data = await ses.send(new SendEmailCommand(params));
        console.log('Success', data)
    }
    catch(err){
        console.log('Error', err);
    }
}
run();
// snippet-end:[ses.JavaScript.v3.email.sendEmail]
exports.run = run; //for unit tests only
