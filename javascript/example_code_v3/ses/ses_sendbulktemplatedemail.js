/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ses-examples-sending-email.html

Purpose:
ses_sendbulktemplatedemail.js demonstrates how to compose an Amazon SES templated email for
multiple destinations and queue it for sending.

Inputs:
- REGION (in commmand line input below)
- RECEIVER_ADDRESS (in commmand line input below)
- SENDER_ADDRESS (in commmand line input below)
- TEXT_FORMAT_BODY (replace in code): Body content of email.
- EMAIL_SUBJECT (replace in code): Subject of email.
- CcAddresses (replace in code; optional): Additional receiver addressses.
- ReplyToAddresses (replace in code; optional): Additional addresses automatically added to replys.

Running the code:
node ses_sendbulktemplatedemail.js REGION
 */
// snippet-start:[ses.JavaScript.email.sendBulkTemplatedEmail]
async function run() {
  try {
    const {SES, ListTemplatesCommand} = require("@aws-sdk/client-ses");
    const region = process.argv[2];
    const ses = new SES(region);
    const params = {
      Destinations: [ /* required */
        {
          Destination: { /* required */
            CcAddresses: [

              /* more items */
            ],
            ToAddresses: [
             process.argv[3]
              /* more items */
            ]
          },
          ReplacementTemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }'
        },
      ],
      Source: process.argv[4], /* required */
      Template: process.argv[5], /* required */
      DefaultTemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }',
      ReplyToAddresses: [

      ]
    };
    const data = await ses.send(new SendBulkTemplatedEmailCommand({params}));
    console.log(data)
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.email.sendBulkTemplatedEmail]
exports.run = run;
