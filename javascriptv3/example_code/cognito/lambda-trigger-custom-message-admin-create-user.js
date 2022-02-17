/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-custom-message.html.

Purpose:
lambda-trigger-custom-message-admin-create-user.js is a custom message AWS Lambda function with the
CustomMessage_AdminCreateUser trigger that returns a user name and verification code. So include
both request.usernameParameter and request.codeParameter in the message body.

Running the code:
1. On the AWS Lambda service dashboard, choose Create function.
2. On the Create function page, name the function, and choose Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the Amazon Cognito service.
5. Choose Manage user pools.
6. Choose the user pool you want to add the trigger to. (If you don't have a user pool, create one.)
7. In General Settings, choose Triggers.
8. In the Custom Message pane, select the Lambda function.
*/

// snippet-start:[cognito.javascript.lambda-trigger.custom-message-admin-create-userV3]
exports.handler = async (event, context) => {
  try {
    if (event.userPoolId === "theSpecialUserPool") {
      // Identify this function was invoked
      if (event.triggerSource === "CustomMessage_AdminCreateUser") {
        // Ensure that your message contains event.request.codeParameter event.request.usernameParameter.
        // This is the placeholder for the code and user name that will be sent to your user.
        event.response.smsMessage =
          "Welcome to the service. Your user name is " +
          event.request.usernameParameter +
          " Your temporary password is " +
          event.request.codeParameter;
        event.response.emailSubject = "Welcome to the service";
        event.response.emailMessage =
          "Welcome to the service. Your user name is " +
          event.request.usernameParameter +
          " Your temporary password is " +
          event.request.codeParameter;
      }
      // Create custom messages for other events

      // Customize messages for other user pools
    }
  } catch (err) {
    // Return to Amazon Cognito
    return null;
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.custom-message-admin-create-userV3]
