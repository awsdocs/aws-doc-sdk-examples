/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-post-confirmation.html.

Purpose:
lambda-trigger-post-confirmation.js is an AWS Lambda function that sends a confirmation email message to your user
using Amazon SES.


Inputs (replace in code):

Running the code:
Running the code:
1. On the AWS Lambda service dashboard, click Create function.
2. On the Create function page, name the function, and click Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the AWS Cognito service.
5. Click Manage User pools.
6. Click the User Pool you want to add the trigger to. (If you don't have a User Pool, create one.)
7. In General Settings, click Triggers.
8. In the Post confirmation pane, select the lambda function.
*/

// snippet-start:[cognito.javascript.lambda-trigger.post-confirmationV3]

// Import required AWS SDK clients and commands for Node.js
const { SES, SendEmailCommand } = require("@aws-sdk/client-ses");
// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"
var ses = new SES();

exports.handler = async (event, context) => {
  console.log(event);
  if (event.request.userAttributes.email) {
    await sendTheEmail(
      event.request.userAttributes.email,
      "Congratulations " + event.userName + ", you have been confirmed:"
    );
    {
      // Return to Amazon Cognito
      return null, event;
    }
  } else {
    // Nothing to do, the user's email ID is unknown
    return null, event;
  }
};

const sendTheEmail = async (to, body) => {
  const eParams = {
    Destination: {
      ToAddresses: [to],
    },
    Message: {
      Body: {
        Text: {
          Data: body,
        },
      },
      Subject: {
        Data: "Cognito Identity Provider registration completed",
      },
    },
    // Replace source_email with your SES validated email address
    Source: "<source_email>",
  };
  try {
    const email = await ses.send(new SendEmailCommand(eParams));
    console.log("===EMAIL SENT===");
  } catch (err) {
    console.log(err);
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.post-confirmationV3]
