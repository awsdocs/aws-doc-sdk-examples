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
sns_publishsms.js demonstrates how to use Amazon SNS to send an SMS text message to a phone number.

Inputs:
- REGION (in commmand line input below)
- TEXT_MESSAGE (in code): The text message to send.
- PHONE_NUMBER  (in commmand line input below): In the E.164 phone number structure

Running the code:
node sns_publishsms.js REGION PHONE_NUMBER
 */
// snippet-start:[sns.JavaScript.SMS.publish]
async function run() {
  try {
    const {SNS, PublishCommand} = require("@aws-sdk/client-sns");
    const region = process.argv[2];
    const sns = new SNS(region);
    const params = {
      Message: 'TEXT_MESSAGE', /* required */
      PhoneNumber: process.argv[3],
    };
    const data = await sns.send(new PublishCommand(params));
    console.log("MessageID is " + data.MessageId);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.SMS.publish]
exports.run = run;
